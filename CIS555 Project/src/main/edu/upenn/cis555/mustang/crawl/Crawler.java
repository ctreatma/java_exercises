package edu.upenn.cis555.mustang.crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import edu.upenn.cis555.mustang.common.Queue;
import edu.upenn.cis555.mustang.index.DistributedHashKey;
import edu.upenn.cis555.mustang.peer.CrawlerApp;

public class Crawler implements Runnable {
//	private String startURL;
	private int maxPages;
	public static final String DISALLOW = "Disallow:";
	private int numSearched;
	private DistributedHashKey hashKey;
	
	public Set<byte[]> searchedURLS;
	public Set<byte[]> seenDigests;
	
	public Set<String> seenURLs;
	
	public Queue<String> toSearch;
	public Queue<Document> docQueue;
	
	private CrawlerApp app;

	public Crawler(int pages, Queue<Document> queue, DistributedHashKey hashKey, CrawlerApp ca){
        this.hashKey = hashKey;
	//	startURL = url;
		seenDigests = new HashSet<byte[]>();
		seenURLs = new HashSet<String>();
		maxPages = pages;
		numSearched = 0;
		toSearch = new Queue<String>();
		searchedURLS = new HashSet<byte[]>();
	//	toSearch.enqueue(url);
		docQueue = queue;
		app = ca;
		app.setCrawler(this);
	}
	
	public void run(){
		System.out.println("Welcome to MuSTanG search");
		long start = 0;
		while(true){
			if(numSearched>=maxPages){
				break;
			}
			String url = toSearch.dequeue();
	//		System.out.println("Dequeued: " + url);
			start = System.currentTimeMillis();
			storeDocument(url);
			System.out.println("URL: " + url);
			//Get time details
			long elapsed = System.currentTimeMillis() - start;
//			float elapsedTimeMin = elapsed/(60*1000F);
			System.out.println("Seconds for page: " + (elapsed / 1000F) + ", number of pages crawled: " + numSearched);
		}
		System.out.println("Done crawling");
	}

	public void storeDocument(String Url) {
		URL url;
//		System.out.println("Looking at: " + Url);
		long start = System.currentTimeMillis();
		MessageDigest md = null;
		byte[] urlHash = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(Url.getBytes());
			urlHash = md.digest();
			md.reset();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(urlHash != null && hasBeenSearched(urlHash)){ // || seenURLs.contains(Url)
	//		System.out.println("Already seen: " + Url);
			return;
		}
		System.out.println("Has URL been seen?: " + (System.currentTimeMillis() - start));
		
		try{
			url= new URL(Url);
		} catch(MalformedURLException e){
			e.printStackTrace();
			return;
		}
		if(url.getProtocol().compareTo("http")!=0 && url.getProtocol().compareTo("https")!=0){
	//		System.out.println("Not HTTP: " + Url);
			return;
		}
		start = System.currentTimeMillis();
		if(!robotOK(url)){
	//		System.out.println("Not allowed by robots.txt: " + Url);
			return;
		}
		System.out.println("Robots.txt Checked: " + (System.currentTimeMillis() - start));
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("HEAD");
			connection.connect();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
//		String contType = connection.getContentType();
		int contLen = connection.getContentLength();
		//Add check if document has been modified since last crawl?
//		long lastMod = connection.getLastModified();
		connection.disconnect();
		
	//	if(!compatibleURL(contType)){
	//		return;
	//	}
		
	//	System.out.println("Store Document: "+url);
		BufferedReader urlBR;
		try {
			urlBR = new BufferedReader(new InputStreamReader(url.openStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		start = System.currentTimeMillis();
            String doc;
		StringBuilder buffer = new StringBuilder();
		List<String> link = new ArrayList<String>();
		String newLine;
//		System.out.println("Reading: " + Url);
		try {
			while(null!=(newLine=urlBR.readLine())){
				buffer.append(newLine);
				// link.addAll(extractlink(newLine, url));
			}
		}
		catch (IOException e) {
				e.printStackTrace();
		}
            doc = buffer.toString();
            System.out.println("Document built: " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();
		link = extract(Url, doc);
	//	System.out.println("Done Reading: " + Url);
		System.out.println("Links extracted: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		//Check if distributed system has seen this Digest
		byte[] digest = null;
		try{
			md.update(doc.getBytes());
			MessageDigest d1 = (MessageDigest)md.clone();
			digest = d1.digest();
			d1.reset();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Digest Computed: " + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		if(!hasSeenDigest(digest) && !peerDigestSeen(digest)) {
			System.out.println("Peers Checked: " + (System.currentTimeMillis() - start));
			Document currDoc = new Document();
			currDoc.setPage(doc);
			currDoc.setUrl(url.toString());
			currDoc.setSize(contLen);
			docQueue.enqueue(currDoc);
			seenDigests.add(digest);
			processLinks(link);
			start = System.currentTimeMillis();
			writeLinksToFile(url, link);
			System.out.println("File written: " + (System.currentTimeMillis() - start));
			numSearched++;
		}
		else {
			System.out.println("Peers Checked: " + (System.currentTimeMillis() - start));
	//		System.out.println("Already seen: " + Url);
		}
		searchedURLS.add(urlHash);
	//	seenURLs.add(Url);
	}
	
	private boolean hasBeenSearched(byte[] urlHash) {
		for(byte[] b : searchedURLS)
			if(Arrays.equals(b, urlHash))
				return true;
		return false;
	}

	private void writeLinksToFile(URL url, List<String> link) {
	    try {
	        String sourceId = hashKey.getId(url.toString());
	        File outputDir = new File("linkStructure"); // TODO:  make this a parameter
	        outputDir.mkdirs();
	        File linkStructure = new File(outputDir.getPath() + "/links" + sourceId + ".txt");
	        linkStructure.createNewFile();
	        FileOutputStream out = new FileOutputStream(linkStructure);

	        for (String linkUrl : link) {
	            String structure = sourceId + " " + hashKey.getId(linkUrl) + "\n";
	            out.write(structure.getBytes());
	        }

	        out.flush();
	        out.close();
	    }
	    catch (IOException e) {
            // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
    }
/*
    public List<String> extractlink(String docLine, URL url){
		Matcher m = Pattern.compile("HREF=\".*?\"", Pattern.CASE_INSENSITIVE).matcher(docLine);
		ArrayList<String> links = new ArrayList<String>();
		while(m.find()){
			String link = docLine.substring(m.start()+6, m.end()-1);
		//	System.out.println("\tLink: " + link);
			if(link.startsWith("http://"))
				links.add(link);
			else if(!link.startsWith("#")){
				//if(link.startsWith("/"))
				//	link = link.substring(1);
				try {
					link = link.toLowerCase();
					if(!link.startsWith("javascript") && link.length()>0 && link.charAt(0)!='\'') {
						URL linkURL = new URL(url, link);
			//			System.out.println("\tAdding: " + linkURL.toString());
						links.add(linkURL.toString());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return links;	
	}
*/
    private List<String> extract(String urlPath, String page) {
    	List<String> links = new ArrayList<String>();
		String path;
		if (urlPath.indexOf("/", urlPath.indexOf("://") + "://".length()) > -1) {
			path = urlPath;
		} else {
			path = urlPath + "/";
		}
		URI uri = URI.create(path);
		URI altUri = path.endsWith("/") ? null : URI.create(path + "/");
		
		String[] fragments = page.split("</[aA]\\s*>");
		for (String fragment : fragments) {
			if (fragment.matches("(?i).*<a\\b.*href=\"([^\"]*)\".*>.*")) {
				String link = fragment.replaceFirst("(?i).*<a\\b", "").replaceFirst(">.*", "")
					.replaceFirst("(?i).*href=\"([^\"]*)\".*", "$1").trim();
				if (link.length() != 0 && !link.startsWith("#") && 
					!link.matches("(?i)^javascript:.*") && !link.matches("(?i)^mailto:.*")) {
					try {
						link = link.replaceAll("\\s", "%20").replaceAll("&amp;", "&");
						URI linkUri = new URI(link);
						URI resolvedUri = uri.resolve(linkUri);
						links.add(resolvedUri.toString());
						if (altUri != null && !linkUri.isAbsolute()) {
							resolvedUri = altUri.resolve(linkUri);
							links.add(resolvedUri.toString());
						}
					} catch (URISyntaxException e) { }	
				} 	
			}
		}
    	return links;
    }
	
	//Checks to see if the URL matches the needed type and size
	public boolean compatibleURL(String type){
			if(type.compareTo("text/html")==0){
				return true;
			}
		return false;
	}
	
	public boolean robotOK(URL url){
		String strHost = url.getHost();
		//Get the location of the robot.txt file from the URL
		String robotStr = "http://" + strHost + "/robots.txt";
		URL robotURL;
		try { 
		    robotURL = new URL(robotStr);
		} catch (MalformedURLException e) {
		    return false;
		}

		String strCommands = "";
		try {
		    InputStream urlRobotStream = robotURL.openStream();

		    // read in entire file
		    byte b[] = new byte[1000];
		    int numRead = urlRobotStream.read(b);
		    if(numRead != -1) {
		    	strCommands = new String(b, 0, numRead);
		    }
		    //Reads all of the file in blocks of 1000 bytes
		    while (numRead != -1) {
		    	numRead = urlRobotStream.read(b);
		    	if (numRead != -1) {
		    		String newCommands = new String(b, 0, numRead);
		    		strCommands += newCommands;
		    	}
		    }
		    urlRobotStream.close();
		} catch (StringIndexOutOfBoundsException e) {
			return false;
		} catch(IOException e) {
			return false;
		}

		String strURL = url.getFile();
		int index = 0;
		while ((index = strCommands.indexOf(DISALLOW, index)) != -1) {
		    index += DISALLOW.length();
		    String strPath = strCommands.substring(index);
		    StringTokenizer st = new StringTokenizer(strPath);
		    if (!st.hasMoreTokens())
		    	break;
		   
		    String strBadPath = st.nextToken();
		    if (strURL.indexOf(strBadPath) == 0)
		    	return false;
		}
		return true;
	}
    
    public boolean hasSeenDigest(byte[] digest) {
    	for(byte[] b : seenDigests)
			if(Arrays.equals(b, digest))
				return true;
		return false;
	}

	public boolean peerDigestSeen(byte[] digest) {
		/* try {
			app.digestQuery(digest);
			synchronized (this) {
				while(!app.getReady())
					this.wait();
			}
	//		System.out.println("awake");
			return app.digestSeen();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} */
	    return false;
	}
	
	public void processLinks(List<String> links) {
	//	List<String> keep = app.sendLinks(links);
	//	for(String s : keep)
	//		toSearch.enqueue(s);
		long start = System.currentTimeMillis();
		app.sendLinks(links);
		System.out.println("Links Sent: " + (System.currentTimeMillis() - start));
	}
	
	public void enqueue(List<String> url) {
		for(String u : url)
			toSearch.enqueue(u);
	}
}
