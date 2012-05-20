package edu.upenn.cis555.mustang.search;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.upenn.cis555.mustang.common.SearchResult;

public class RestClient {
	private static final String GET = "GET";
//	private static final String POST = "POST";
	static final String HTTP_VERSION = "HTTP/1.1";
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	static final String DEFAULT_CONTENT_TYPE = "application/xml";
	private static final String REST_BODY_PATTERN = "<\\?xml\\s+.+\\?>"; 
	
	private String searchEngineHost;
	private int searchEnginePort;
	
	public RestClient(String searchEngineHost, int searchEnginePort) {
		this.searchEngineHost = searchEngineHost;
		this.searchEnginePort = searchEnginePort;
	}
	
	public SubCollection<RankedDocument> connect(String uri) throws IOException {
		//1. open a socket to connect to the server
		Socket socket = new Socket(searchEngineHost, searchEnginePort);
		//2. get input and output streams
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));            
        DataOutputStream out = new DataOutputStream(socket.getOutputStream()); 
		out.flush();
		//3: communicate with the server
		marshalHttpRequest(uri, out);
		SubCollection<RankedDocument> rankedDocs = unmarshalHttpResponse(in); 
		in.close();
		socket.close();
		return rankedDocs;
	}

	private void marshalHttpRequest(String uri, DataOutputStream out) throws IOException {
		StringBuilder builder = new StringBuilder();
		String path = uri.substring(uri.indexOf("/", uri.indexOf("://") + "://".length()));
		builder.append(GET).append(" ").append(path).append(" ").append(HTTP_VERSION).append(LINE_SEPARATOR);
		builder.append("Host: ").append(searchEngineHost).append(LINE_SEPARATOR);
		builder.append("Accept: ").append(DEFAULT_CONTENT_TYPE).append(LINE_SEPARATOR);
		builder.append(LINE_SEPARATOR);
		out.writeBytes(builder.toString());
		out.flush();
	}
	
	private SubCollection<RankedDocument> unmarshalHttpResponse(BufferedReader in) throws IOException {
		String line;
		StringBuilder body = new StringBuilder();
		while ((line = in.readLine()) != null && line.length() > 0) {
			if (line.matches(REST_BODY_PATTERN) || body.length() > 0) {
				body.append(line);
			}
		}
		
		List<RankedDocument> results = null;
		try {
			results = new ArrayList<RankedDocument>();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(body.toString())));
			Node parent = document.getDocumentElement(); //search results
			NodeList nodes = parent.getChildNodes(); // count and search result
			for (int i = 0; i < nodes.getLength(); i++) {
				RankedDocument rankedDoc = null;
				String title = null, url = null, blurb = null, score = null;
				NodeList items = nodes.item(i).getChildNodes(); // child nodes under search result
				for (int j = 0; j < items.getLength(); j++) { // child node
					String name = items.item(j).getNodeName();
					String value = items.item(j).getChildNodes().item(0) == null ? "" :
						items.item(j).getChildNodes().item(0).getNodeValue();
					if ("title".equals(name)) {
						title = value;
					} else if ("url".equals(name)) {
						url = value;
					} else if ("blurb".equals(name)) {
						blurb = value;
					} else if ("rank".equals(name)) {
						score = value;
					}
				}
				rankedDoc = new RankedDocument(new SearchResult(title, url, blurb));
				DocumentRank rank = new DocumentRank();
				rank.setScore(Double.parseDouble(score));
				rankedDoc.setRank(rank);
				results.add(rankedDoc);
			}
			int count = Integer.parseInt(parent.getAttributes().getNamedItem("count").getChildNodes().item(0).getNodeValue());	
			return new SubCollection<RankedDocument>(results, count);
		} catch (ParserConfigurationException e) {
		} catch (SAXException e) { }
		return null;
	}
}
