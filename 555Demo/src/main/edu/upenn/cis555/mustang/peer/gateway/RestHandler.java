package edu.upenn.cis555.mustang.peer.gateway;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.upenn.cis555.mustang.peer.IndexApp;
import edu.upenn.cis555.mustang.peer.MessageType;
import edu.upenn.cis555.mustang.peer.Request;
import edu.upenn.cis555.mustang.rank.PageRank;
import edu.upenn.cis555.mustang.webserver.support.HttpServletResponseSupport;

public class RestHandler {
	private static final String HTTP_1_0 = "HTTP/1.0";
	private static final String HTTP_1_1 = "HTTP/1.1";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_HEAD = "HEAD";
	private static final String INITIAL_REQUEST_LINE_PATTERN = 
		"(" + METHOD_GET + "|" + METHOD_POST + "|" + METHOD_HEAD + ")\\s+[^\\s]+\\s+(HTTP/)\\d\\.\\d";
	private static final String HEADER_HOST_PATTERN = "(Host:)\\s+[^\\s]+";
	private static final String HEADER_COOKIE_PATTERN = "(Cookie:)\\s+([^\\s]+=[^\\s]+;\\s)*([^\\s]+=[^\\s]+)";
	private static final String HEADER_CONTENT_LENGTH_PATTERN = "(Content-Length:)\\s+\\d+";
	private static final String HEADER_PATTERN = "[^\\s]+:\\s+.+";
	private static final String BLANK_LINE_PATTERN = "\\s*";
	private static final String MESSAGE_BODY_PATTERN = "(([^\\s]+=[^\\&]*)&*)+";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String DEFAULT_CONTENT_TYPE = "text/html";
	public static final int UNKNOWN_CONTENT_LENGTH = -1;
	
	private static Calendar calendar;
	private static DateFormat headerDateFormat;
	static {
		calendar = GregorianCalendar.getInstance();
		headerDateFormat = new SimpleDateFormat("EEE, d MMM yyyy H:mm:ss z");
		headerDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	private BufferedReader input;
	private DataOutputStream output;
	private String version;
	private String method;
	private String uri;
	private boolean host;
	private Map<String, String> headers;
	private int contentLength;
	private IndexApp indexApp;
	
	public RestHandler(IndexApp indexApp, BufferedReader input, DataOutputStream output) {
	    this.indexApp = indexApp;
		this.input = input;
		this.output = output;
	}

	/**
	 * Handles header line in HTTP request
	 * @param line
	 * @return content length if method POST, otherwise UNKNOWN_CONTENT_LENGTH
	 * @throws IOException
	 */
	public int handleRequest(String line) throws IOException {
		if (Pattern.matches(INITIAL_REQUEST_LINE_PATTERN, line)) {
			String[] initialRequest = line.split("\\s+");
			method = initialRequest[0];
			uri = initialRequest[1];
			version = initialRequest[2];
			headers = new HashMap<String, String>();
			contentLength = UNKNOWN_CONTENT_LENGTH;
			// signify receiving request from HTTP 1.1 client 
			handleContinue();
		} else if (Pattern.matches(HEADER_HOST_PATTERN, line)) {
			host = true;
		}
		if (Pattern.matches(HEADER_PATTERN, line) && !Pattern.matches(HEADER_COOKIE_PATTERN, line)) {
			headers.put(line.substring(0, line.indexOf(":")), line.substring(line.indexOf(":") + 1).trim());
		}
		if (Pattern.matches(HEADER_CONTENT_LENGTH_PATTERN, line)) {
			contentLength = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
		}
		return contentLength;
	}
	
	public void handleResponse(String body) throws IOException {
		if (HTTP_1_1.equals(version) && !host) {
			handleVersion();
			return;
		}
		if (METHOD_GET.equals(method) || METHOD_POST.equals(method) || METHOD_HEAD.equals(method)) {
			try {
				handleSupported(body);
			} catch (IOException e) {
				handleServerError();
			}
		} else {
			handleUnsupported();
		}
		output.flush();
		output.close();
	}

	private void handleSupported(String body) throws IOException {
	    if (uri.compareTo(PageRank.REST_PATH) == 0) {
	        if (METHOD_POST.equals(method)) { // The REST PageRank service is only for POSTing PageRanks to the datastore
	            List<Request> requests = unmarshalHttpRequest(body);
	            MessageType messageType = MessageType.RANK;

	            for (Request request : requests) {
	                indexApp.send(indexApp.getNodeFactory().getId(request.getDocId()), request, messageType);
	            }

	            output.writeBytes(HttpServletResponseSupport.getStatusLine(version, 200));
	            output.writeBytes("Date: " + headerDateFormat.format(calendar.getTime()) + LINE_SEPARATOR);
	            output.writeBytes("Content-Type: " + DEFAULT_CONTENT_TYPE + LINE_SEPARATOR);
	            output.writeBytes("Content-Length: 0" + LINE_SEPARATOR);
	            output.writeBytes("Connection: close" + LINE_SEPARATOR);
	            output.writeBytes(LINE_SEPARATOR);
	        }
	        else {
	            handleUnsupported();
	        }
	    }
	    else {
	        handleForbidden();
	        return;
	    }
	}

	private void handleUnsupported() throws IOException {
		createStatusCodeMessage(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}
	
	private void handleVersion() throws IOException {
		createStatusCodeMessage(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	private void handleContinue() throws IOException {
		if (!HTTP_1_0.equals(version)) {
			output.writeBytes(version + " " + HttpServletResponse.SC_CONTINUE + LINE_SEPARATOR);
			output.writeBytes(LINE_SEPARATOR);
		}
	}
	
	private void handleForbidden() throws IOException {
		createStatusCodeMessage(HttpServletResponse.SC_FORBIDDEN);
	}
	
	private void handleServerError() throws IOException {
		createStatusCodeMessage(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
	
	private void createStatusCodeMessage(int statusCode) throws IOException {
		StringBuilder page = new StringBuilder();
		page.append("<html><head><title>Error ");
		page.append(HttpServletResponseSupport.STATUS_CODES.get(statusCode));
		page.append("</title></head><body><h2>HTTP Error: ");
		page.append(HttpServletResponseSupport.STATUS_CODES.get(statusCode));
		page.append("</h2></body></html>");		
		output.writeBytes(HttpServletResponseSupport.getStatusLine(version, statusCode));
		output.writeBytes("Date: " + headerDateFormat.format(calendar.getTime()) + LINE_SEPARATOR);
		output.writeBytes("Content-Type: " + DEFAULT_CONTENT_TYPE + LINE_SEPARATOR);
		output.writeBytes("Content-Length: " + page.toString().getBytes().length + LINE_SEPARATOR);
        output.writeBytes("Connection: close" + LINE_SEPARATOR);
		output.writeBytes(LINE_SEPARATOR);
		output.writeBytes(page.toString());
	}
	
	   
    private List<Request> unmarshalHttpRequest(String body) throws IOException {
        List<Request> requests = null;
        try {
            requests = new ArrayList<Request>();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(body.toString())));
            Node parent = document.getDocumentElement(); // pageRanks
            NodeList nodes = parent.getChildNodes(); // pageRank
            double rank = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
                Request request = new Request();
                NodeList items = nodes.item(i).getChildNodes(); // child nodes under pageRank
                for (int j = 0; j < items.getLength(); j++) { // child node
                    String name = items.item(j).getNodeName();
                    String value = items.item(j).getChildNodes().item(0) == null ? "" :
                        items.item(j).getChildNodes().item(0).getNodeValue();
                    if ("docId".equals(name)) {
                        request.setDocId(value);
                    } else if ("rank".equals(name)) {
                        request.setPageRank(Double.parseDouble(value));
                        rank = request.getPageRank();
                    }
                }
                requests.add(request);
            }
            System.out.println("Got " + requests.size() + " documents with pageRank " + rank);
        } catch (ParserConfigurationException e) {
        } catch (SAXException e) { }
        return requests;
    }
}
