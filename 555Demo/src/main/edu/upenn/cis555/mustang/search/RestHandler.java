package edu.upenn.cis555.mustang.search;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RestHandler {
	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";
	private static final String INITIAL_REQUEST_LINE_PATTERN = 
		"(" + METHOD_GET + "|" + METHOD_POST + "|" + ")\\s+[^\\s]+\\s+(HTTP/)\\d\\.\\d";
	
	private Request request;
	
	Request handleRequest(String line) throws IOException {
		if (Pattern.matches(INITIAL_REQUEST_LINE_PATTERN, line)) {
			String[] initialRequest = line.split("\\s+");
			String uri = initialRequest[1];
			String param = uri.substring(uri.lastIndexOf("/") + 1);
			request = new Request(URLDecoder.decode(param, "UTF-8"));
		}
		return request;
	}
	
	String handleResponse(Response response) {
		String xml = null;
    	try {
    		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    		Document document = builder.newDocument();
    	    Element root = document.createElement("results");
    	    root.setAttribute("count", String.valueOf(response.getResultCount()));
    	    document.appendChild(root);
    		List<RankedDocument> results = response.getResults();
    		for (RankedDocument result : results) {
	    		Element parent = document.createElement("result");
	    		root.appendChild(parent);
	    		
	    		Element title = document.createElement("title");
	    		title.setTextContent(result.getTitle());
	    		parent.appendChild(title);
	    		
	    		Element url = document.createElement("url");
	    		url.setTextContent(result.getUrl());
	    		parent.appendChild(url);
	    		
	    		Element blurb = document.createElement("blurb");
	    		blurb.setTextContent(result.getBlurb());
	    		parent.appendChild(blurb);
	    		
	    		Element rank = document.createElement("rank");
	    		rank.setTextContent(String.valueOf(result.getRank().getScore()));
	    		parent.appendChild(rank);
    		}
    		StreamResult result = new StreamResult(new StringWriter());
    		getTransformer().transform(new DOMSource(document), result);
    		xml = result.getWriter().toString();
		} catch (ParserConfigurationException e) {
			System.out.println("parser configuration error: " + e);
		} catch (TransformerException e) {
			System.out.println("transformer error: " + e);
		}
		return xml;
	}
	
	private Transformer getTransformer() {
		Transformer transformer = null;
		try {
			TransformerFactory factory = TransformerFactory.newInstance(); 
			transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		} catch (TransformerConfigurationException e) { }
		return transformer;
	}
}
