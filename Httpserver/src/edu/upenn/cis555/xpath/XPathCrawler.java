package edu.upenn.cis555.xpath;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Queue;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

import edu.upenn.cis555.db.Channel;
import edu.upenn.cis555.db.CrawledURL;
import edu.upenn.cis555.db.XPathDB;

public class XPathCrawler {
    private static final String USER_AGENT = "cis555crawler";
    private static final int SIZE_FACTOR = 1024 * 1024;

    private Queue<String> urlsToCrawl;
    private XPathDB database;
    private int maxSize;
    private int maxVisited;
    private UUID crawlId = UUID.randomUUID();
    private int numVisited;
    private Tidy tidy;

    public XPathCrawler(XPathDB database, int maxSize, int maxFilesVisited) throws IOException {
        this.database = database;
        urlsToCrawl = new LinkedList<String>();
        this.maxVisited = maxFilesVisited;
        this.maxSize = maxSize;

        tidy = new Tidy();
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);
        tidy.setShowErrors(0);
    }

    public int getNumVisited() {
        return numVisited;
    }
    
    public void crawl(String url) {
        urlsToCrawl.add(url);
        // Recheck all urls that were crawled already
        updateCrawledUrls();
        // Start the actual crawl
        crawl();
    }
    
    void updateCrawledUrls() {
        ArrayList<CrawledURL> crawledUrls = database.getAllCrawledURLs();
        for (CrawledURL crawledUrl : crawledUrls) {
            if (crawledUrl.getContent() != null) {
                handleUnmodifiedUrl(crawledUrl);
            }
        }
    }

    void crawl() {
        while (urlsToCrawl.size() > 0 && (maxVisited < 0 || numVisited < maxVisited)) {
            try {
                URL url = new URL(urlsToCrawl.remove());
                if (robotsAllowed(url)) {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("HEAD");

                    CrawledURL crawledUrl = database.getCrawledURL(url.toString());
                    if (crawledUrl != null) {
                        if (crawledUrl.getLastCrawlId().compareTo(crawlId.toString()) != 0) {
                            if (crawledUrl.getContent() != null) {
                                // Only retrieve file if it's content is not stored or has been modified since the last crawl
                                SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
                                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                                conn.setRequestProperty("If-Modified-Since", format.format(crawledUrl.getLastCrawled()));
                            }
                        }
                        else {
                            // Already visited this link on this crawl, so skip it
                            continue; 
                        }
                    }
                    conn.connect();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        if (conn.getContentLength() <= (maxSize * SIZE_FACTOR)) {
                            String contentType = conn.getContentType();
                            if (contentType.contains("text/xml") ||
                                contentType.contains("application/xml") ||
                                contentType.contains("+xml") ||
                                contentType.equalsIgnoreCase("text/html")) {
                                numVisited++;
                                
                                if (crawledUrl == null) {
                                    crawledUrl = new CrawledURL(url.toString(), new Date(), crawlId.toString(), null);
                                    database.addCrawledURL(crawledUrl);
                                }
                                else {
                                    crawledUrl.setLastCrawled(new Date());
                                    crawledUrl.setLastCrawlId(crawlId.toString());
                                    database.updateCrawledURL(crawledUrl);
                                }
                                
                                if (contentType.equalsIgnoreCase("text/html")) {
                                    handleHtml(url);
                                }
                                else {
                                    handleXml(url);
                                }
                            }
                        }
                    }
                }
            }
            catch (MalformedURLException ex) {
                ex.printStackTrace();
                // TODO: Log this error?
            }
            catch (IOException ex) {
                // TODO: Log this error?
            }
        }
    }

    boolean robotsAllowed(URL url) {
        try {
            String robotsUrl;
            String host = url.getHost();
            int port = url.getPort();
            if (port >= 0) {
                robotsUrl = "http://" + host + ":" + port;
            }
            else {
                robotsUrl = "http://" + host;
            }
            URL robots = new URL(robotsUrl + "/robots.txt");

            HttpURLConnection conn = (HttpURLConnection) robots.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-agent", USER_AGENT);
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                String currentRobot = null;
                Map<String, List<String>> robotsMap = new HashMap<String,List<String>>();
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("User-agent:")) {
                        String[] parts = line.split("User-agent:");
                        if (parts.length > 1) {
                            parts[1].trim().toLowerCase();
                            if (!robotsMap.containsKey(currentRobot)) {
                                robotsMap.put(currentRobot, new ArrayList<String>());
                            }
                        }
                    }
                    else if (line.startsWith("Disallow:")) {
                        String[] parts = line.split("Disallow:");
                        if (parts.length > 1) {
                            if (currentRobot == null) {
                                return true; // Malformed robots.txt, assume welcome
                            }
                            else {
                                robotsMap.get(currentRobot).add(parts[1].trim());
                            }
                        }
                    }
                }
                List<String> paths = robotsMap.get(USER_AGENT.toLowerCase());
                if (paths == null) {
                    paths = robotsMap.get("*");
                }

                if (paths != null) {
                    for (String path : paths) {
                        if (url.getPath().startsWith(path)) {
                            return false;
                        }
                    }
                }
            }
            return true; // Couldn't get robots.txt, it didn't mention us, or didn't mention path: assume welcome
        }
        catch (MalformedURLException ex) {
            return false; // Best to avoid the url that led to this.
        }
        catch (IOException ex) {
            return false; // Best to avoid the url that led to this.
        }
    }

    void handleHtml(URL url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Document document = tidy.parseDOM(conn.getInputStream(), null);
                visitLinks(document.getDocumentElement(), url);
                testXPaths(document, url.toString());
            }
        }
        catch (IOException ex) {
            // TODO:  Log this exception?
        }
    }

    void visitLinks(Node node, URL currentPage) {
        if (node.getNodeName().equalsIgnoreCase("a")) {
            String url = ((Element) node).getAttribute("href");
            try {
                if (!url.startsWith("http://")) {
                    url = currentPage.toURI().resolve(url).toString();
                }
                if (url.startsWith("http://")) {
                    // Avoid adding javascript: urls, etc.
                    urlsToCrawl.add(url);
                }
            } catch (URISyntaxException e) {
                System.err.println("ERROR: resolving uri: " + url);
                // TODO Log this error?
            } catch (IllegalArgumentException e) {
                System.err.println("ERROR: resolving uri: " + url);
                // TODO Log this error?
            }
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            visitLinks(children.item(i), currentPage);
        }
    }

    void handleXml(URL url) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.openStream());

            testXPaths(document, url.toString());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            // TODO: Log this error?
        }
        catch (SAXException ex) {
            // TODO: Log this error?
        }
        catch (ParserConfigurationException ex) {
            // TODO: Log this error?
        }
    }
    
    void handleUnmodifiedUrl(CrawledURL url) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(url.getContent().getBytes("UTF-8")));

            testXPaths(document, url.getUrl());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            // TODO: Log this error?
        }
        catch (SAXException ex) {
            // TODO: Log this error?
        }
        catch (ParserConfigurationException ex) {
            // TODO: Log this error?
        }
    }
    
    void testXPaths(Document document, String url) throws IOException {
            ArrayList<Channel> channels = database.getAllChannels();
            for (Channel channel : channels) {
                for (String xpath : channel.getXPathExprs()) {
                    XPathEngine engine = new XPathEngine(xpath);
                    if (engine.evaluate(document)) {
                        CrawledURL crawledURL = database.getCrawledURL(url.toString());
                        List<String> channelNames;
                        if (crawledURL == null) {
                            channelNames = new ArrayList<String>();
                            channelNames.add(channel.getName());
                            crawledURL = new CrawledURL(url, new Date(), crawlId.toString(), channelNames);
                        }
                        else {
                            channelNames = crawledURL.getChannelNames();
                            if (!channelNames.contains(channel.getName())) {
                                channelNames.add(channel.getName());
                            }
                        }
                        crawledURL.setContent(XPathCrawler.getContent(document));
                        database.updateChannel(channel);
                        database.updateCrawledURL(crawledURL);
                    }
                }
            }
    }

    static String getContent(Document document) throws IOException {
        StringBuilder content = new StringBuilder();
        getNodeContent(document.getDocumentElement(), content);
        return content.toString();
    }
    
    private static void getNodeContent(Node node, StringBuilder content) {
        switch (node.getNodeType()) {
        case Node.ATTRIBUTE_NODE:
            break;
        case Node.ELEMENT_NODE:
            content.append("<" + node.getNodeName());
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); ++i) {
                content.append(" " + attributes.item(i).getNodeName() + "='" + attributes.item(i).getNodeValue() + "'");
            }
            content.append(">");
            
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                getNodeContent(children.item(i), content);
            }
            
            content.append("</" + node.getNodeName() + ">");
            break;
        case Node.TEXT_NODE:
            content.append("<![CDATA[" + node.getNodeValue() + "]]>"); // Can't figure out how to re-escape entities.
            break;
        case Node.COMMENT_NODE:
            content.append("<!-- " + node.getNodeValue() + " -->");
            break;
        case Node.CDATA_SECTION_NODE:
            content.append("<![CDATA[" + node.getNodeValue() + "]]>");
            break;
        case Node.ENTITY_REFERENCE_NODE:
            content.append("&" + node.getNodeName() + ";");
            break;
        }
    }

    
    // Main loop
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java.edu.upenn.cis555.XPathCrawler <start url> <berkeley db dir> <max file size> [<max files>]");
            System.exit(1);
        }
        try {
            String startUrl = args[0];
            String pathToDb = args[1];
            Integer maxSize = Integer.parseInt(args[2]);
            Integer maxFiles = null;
            if (args.length > 3) {
                maxFiles = Integer.parseInt(args[3]);
            }
            else {
                maxFiles = -1;
            }
            XPathDB database = new XPathDB(pathToDb);
            XPathCrawler crawler = new XPathCrawler(database, maxSize, maxFiles);
            crawler.crawl(startUrl);
        }
        catch (IOException ex) {
            // TODO: Log this error?
            System.err.println("Usage: java.edu.upenn.cis555.XPathCrawler <start url> <berkeley db dir> <max file size> [<max files>]");
            System.exit(1);
        }
        catch (NumberFormatException ex) {
            // TODO: Log this error?
            System.err.println("Usage: java.edu.upenn.cis555.XPathCrawler <start url> <berkeley db dir> <max file size> [<max files>]");
            System.exit(1);      
        }
    }
}
