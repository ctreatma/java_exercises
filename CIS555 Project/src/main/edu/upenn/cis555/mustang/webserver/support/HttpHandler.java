package edu.upenn.cis555.mustang.webserver.support;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

public class HttpHandler {
	private static final String HTTP_1_0 = "HTTP/1.0";
	private static final String HTTP_1_1 = "HTTP/1.1";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_HEAD = "HEAD";
	private static final String INITIAL_REQUEST_LINE_PATTERN = 
		"(" + METHOD_GET + "|" + METHOD_POST + "|" + METHOD_HEAD + ")\\s+[^\\s]+\\s+(HTTP/)\\d\\.\\d";
	private static final String HEADER_HOST_PATTERN = "(Host:)\\s+[^\\s]+";
	private static final String HEADER_IF_MODIFIED_SINCE_PATTERN = "(If\\-Modified\\-Since:)\\s+";
	private static final String HEADER_IF_UNMODIFIED_SINCE_PATTERN = "(If\\-Unmodified\\-Since:)\\s+";
	private static final String HEADER_PERSISTENT_CONNECTION_PATTERN = "(Connection:)\\s+(Keep\\-Alive)";
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
	private static DateFormat fileDateFormat;
	static {
		calendar = GregorianCalendar.getInstance();
		headerDateFormat = new SimpleDateFormat("EEE, d MMM yyyy H:mm:ss z");
		headerDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		fileDateFormat = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");
	}
		
	private String documentRoot;
	private int port;
	private BufferedReader input;
	private DataOutputStream output;
	private String version;
	private String method;
	private String uri;
	private boolean host;
	private Map<String, String> headers;
	private Date ifModifiedSinceDate;
	private Date ifUnmodifiedSinceDate;
	private String[] cookies;
	private String messageBody;
	private int contentLength;
	// extra credit
	private boolean persistentConnection;
	private boolean commit;
	
	public HttpHandler(int port, String documentRoot, BufferedReader input, DataOutputStream output) {
		this.documentRoot = documentRoot;
		this.port = port;
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
			if (commit) {
				handleResponse();
				host = false;
			} else {
				commit = true;
			}
			String[] initialRequest = line.split("\\s+");
			method = initialRequest[0];
			uri = initialRequest[1];
			version = initialRequest[2];
			headers = new HashMap<String, String>();
			messageBody = null;
			contentLength = UNKNOWN_CONTENT_LENGTH;
			// signify receiving request from HTTP 1.1 client 
			handleContinue();
		} else if (Pattern.matches(HEADER_HOST_PATTERN, line)) {
			host = true;
		} else if (Pattern.matches(HEADER_PERSISTENT_CONNECTION_PATTERN, line)) {
			persistentConnection = true;
		} else if (Pattern.matches(HEADER_IF_MODIFIED_SINCE_PATTERN + ".+", line) || 
			Pattern.matches(HEADER_IF_UNMODIFIED_SINCE_PATTERN + ".+", line)) {
			String ifModifiedSince = line.replaceFirst(HEADER_IF_MODIFIED_SINCE_PATTERN, "");
			String ifUnmodifiedSince = line.replaceFirst(HEADER_IF_UNMODIFIED_SINCE_PATTERN, "");
			DateFormat dateFormat = new SimpleDateFormat("EEE MMM d H:mm:ss yyyy");
			try {
				ifModifiedSinceDate = dateFormat.parse(ifModifiedSince);
				return UNKNOWN_CONTENT_LENGTH;
			} catch (ParseException modified) {
				try {
					ifUnmodifiedSinceDate = dateFormat.parse(ifUnmodifiedSince);
					return UNKNOWN_CONTENT_LENGTH;
				} catch (ParseException unmodified) { }
			}
			dateFormat = headerDateFormat;
			try {
				ifModifiedSinceDate = dateFormat.parse(ifModifiedSince);
				return UNKNOWN_CONTENT_LENGTH;
			} catch (ParseException modified) {
				try {
					ifUnmodifiedSinceDate = dateFormat.parse(ifModifiedSince);
					return UNKNOWN_CONTENT_LENGTH;
				} catch (ParseException unmodified) {}
			}
			dateFormat = new SimpleDateFormat("EEEE, d-MMM-yy H:mm:ss z");	
			try {
				ifModifiedSinceDate = dateFormat.parse(ifModifiedSince);
			} catch (ParseException modified) {
				try {
					ifUnmodifiedSinceDate = dateFormat.parse(ifModifiedSince);
				} catch (ParseException unmodified) { }
			}
		} else if (Pattern.matches(HEADER_COOKIE_PATTERN, line)) {
			// parse "Cookie: n1=v1; n2=v2"
			cookies = line.split(":\\s+|;\\s+|=");
		} else if (Pattern.matches(BLANK_LINE_PATTERN, line)) {
			if (METHOD_GET.equals(method) || METHOD_HEAD.equals(method)) {
				handleResponse();
			} else if (METHOD_POST.equals(method) && contentLength == 0) {
				handleResponse();
			}
		} else if (METHOD_POST.equals(method) && Pattern.matches(MESSAGE_BODY_PATTERN, line)) {
			messageBody = new String(line);
			handleResponse();
		}
		if (Pattern.matches(HEADER_PATTERN, line) && !Pattern.matches(HEADER_COOKIE_PATTERN, line)) {
			headers.put(line.substring(0, line.indexOf(":")), line.substring(line.indexOf(":") + 1).trim());
		}
		if (Pattern.matches(HEADER_CONTENT_LENGTH_PATTERN, line)) {
			contentLength = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
		}
		return contentLength;
	}
	
	public void handleResponse() throws IOException {
		if (HTTP_1_1.equals(version) && !host) {
			handleVersion();
			return;
		}
		if (METHOD_GET.equals(method) || METHOD_POST.equals(method) || METHOD_HEAD.equals(method)) {
			try {
				handleSupported();
			} catch (ServletException e) {
				handleServerError();
			}
		} else {
			handleUnsupported();
		}
		output.flush();
		output.close();
	}
	
	private void handleSupported() throws IOException, ServletException {
		Date current = calendar.getTime();
//		if (uri.startsWith("..") || uri.endsWith("..") || uri.indexOf("../") >= 0) {
		if (uri.indexOf("..") >= 0) {	
			handleForbidden();
			return;
		}

		String[] paths = uri.split("\\?|&|=");
		String path = paths[0];
		HttpServlet servlet = WebUtil.getHttpServlet(path);
		if (servlet == null) {
			if (!path.endsWith("/")) {
				path = path.concat("/");
			}
			
			File resource = new File(documentRoot + path);
			if (resource.exists()) {
				if (ifModifiedSinceDate != null && ifModifiedSinceDate.before(current) && 
					ifModifiedSinceDate.getTime() > resource.lastModified()) {
					handleIfModifiedSince();
				} else if (ifUnmodifiedSinceDate != null && ifUnmodifiedSinceDate.before(current) && 
					ifUnmodifiedSinceDate.getTime() < resource.lastModified()) {
					handleIfUnmodifiedSince();
				} else {
					String contentType = null;
					long contentLength;
					StringBuilder page = new StringBuilder();
					ByteArrayOutputStream outputStream = null;
					if (resource.isDirectory()) {
						File[] files = resource.listFiles();
						page.append("<HTML><HEAD><TITLE>Directory: ");
						page.append(path);
						page.append("</TITLE></HEAD><BODY><H1>Directory: ");
						page.append(path);
						page.append("</H1><TABLE BORDER=0 CELLSPACING=4>");				
						for (File file : files) {
							page.append("<TR><TD><A HREF='").append(path).append(file.getName());
							page.append("'>").append(file.getName()).append("</TD>");
							page.append("<TD ALIGN=right>").append(file.length()).append(" bytes</TD>");
							calendar.setTimeInMillis(file.lastModified());
							page.append("<TD>").append(fileDateFormat.format(calendar.getTime())).append("</TD>");
							page.append("</TR>");
						}
						page.append("</TABLE></BODY></HTML>");
						contentLength = page.toString().getBytes().length;
					} else {
						try {
							FileInputStream inputStream = new FileInputStream(resource);
				            outputStream = new ByteArrayOutputStream();
				            byte[] buffer = new byte[(int) resource.length()];
				            while ((inputStream.read(buffer)) >= 0) {
				            	outputStream.write(buffer);
				            }
				            inputStream.close();
						} catch (FileNotFoundException unlikely) {
							handleNotFound();
							return;
						} catch (IOException ioe) {
							handleServerError();
							return;
						}
						// suffice to use here for figuring out file content type 
						contentType = URLConnection.getFileNameMap().getContentTypeFor(resource.getName());
						contentLength = resource.length();
					}
					if (contentType == null) {
						contentType = DEFAULT_CONTENT_TYPE;
					}
					calendar.setTimeInMillis(resource.lastModified());
					output.writeBytes(HttpServletResponseSupport.getStatusLine(version, HttpServletResponse.SC_OK));
					output.writeBytes("Date: " + headerDateFormat.format(current) + LINE_SEPARATOR);
					output.writeBytes("Last-Modified: " + headerDateFormat.format(calendar.getTime()) + LINE_SEPARATOR);
					output.writeBytes("Content-Type: " + contentType + LINE_SEPARATOR);
					output.writeBytes("Content-Length: " + contentLength + LINE_SEPARATOR);
					// extra credit: persistent connection
					if (!persistentConnection) {
						output.writeBytes("Connection: close" + LINE_SEPARATOR);
					}
					output.writeBytes(LINE_SEPARATOR);
					if (outputStream != null) {
						output.write(outputStream.toByteArray());
					} else {
						output.writeBytes(page + LINE_SEPARATOR);
					}
				}
			} else {
				handleNotFound();
			}
		} else {
			HttpSessionSupport session = null;
			if (cookies != null) {
				for (int i = 1; i < cookies.length - 1; i += 2) {
					if (cookies[i].equals(HttpSessionSupport.JSESSIONID)) {
						session = (HttpSessionSupport) WebUtil.getServletConfig().getServletContext().getAttribute(cookies[i+1]);
						break;
					}
				}
			}
			HttpServletRequestSupport request = new HttpServletRequestSupport(session);
			request.setMethod(method);
			request.setPath(path);
			request.setServerPort(port);
			request.setReader(input);
			// URL parameters (GET)
			for (int i = 1; i < paths.length - 1; i += 2) {
				request.setParameter(paths[i], URLDecoder.decode(paths[i+1], "UTF-8"));
			}
			// header
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				request.addHeader(entry.getKey(), entry.getValue());
			}
			// message body (POST)
			if (messageBody != null) {
				String[] message = messageBody.split("&|="); 
				for (int i = 0; i < message.length - 1; i += 2) {
					request.setParameter(message[i], URLDecoder.decode(message[i+1], "UTF-8"));
				}
			}
			if (cookies != null) {
				for (int i = 1; i < cookies.length - 1; i += 2) {
					Cookie cookie = new Cookie(cookies[i], cookies[i+1]);
					request.addCookie(cookie);
				}
			}
			HttpServletResponseSupport response = new HttpServletResponseSupport(version, output);
			response.setPath(path);
			response.setHostName(request.getServerName());
			response.setServerPort(port);
			servlet.service(request, response);
			if (!response.isCommitted()) {
				response.setStatus(HttpServletResponse.SC_OK);
			}
			response.addHeader("Content-Type", response.getContentType());
			response.setDateHeader("Date", current.getTime());
			// extra credit: persistent connection
			if (!persistentConnection) {
				response.addHeader("Connection", "close");
			}
			session = (HttpSessionSupport) request.getSession(false);
			if (session != null && session.isValid()) {
				Cookie sessionCookie = new Cookie(HttpSessionSupport.JSESSIONID, session.getId());
				sessionCookie.setMaxAge(-1);
				response.addCookie(sessionCookie);
				WebUtil.getServletConfig().getServletContext().setAttribute(session.getId(), session);
			}
			output.writeBytes(LINE_SEPARATOR);
			output.writeBytes(response.getWriterWrapper().toString());
			response.setCommitted(true);
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

	private void handleNotFound() throws IOException {
		createStatusCodeMessage(HttpServletResponse.SC_NOT_FOUND);
	}
	
	private void handleServerError() throws IOException {
		createStatusCodeMessage(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
	
	private void handleIfModifiedSince() throws IOException {
		output.writeBytes(version + " " + HttpServletResponse.SC_NOT_MODIFIED + LINE_SEPARATOR);
		output.writeBytes("Date: " + headerDateFormat.format(calendar.getTime()) + LINE_SEPARATOR);
		output.writeBytes(LINE_SEPARATOR);
	}
	
	private void handleIfUnmodifiedSince() throws IOException {
		output.writeBytes(version + " " + HttpServletResponse.SC_PRECONDITION_FAILED + LINE_SEPARATOR);
		output.writeBytes(LINE_SEPARATOR);
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
		output.writeBytes(LINE_SEPARATOR);
		output.writeBytes(page.toString());
	}
}
