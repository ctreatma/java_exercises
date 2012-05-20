package edu.upenn.cis555.mustang.webserver.support;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class HttpServletResponseSupport implements HttpServletResponse {
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	static final String DEFAULT_CONTENT_TYPE = "text/html";
	public static Map<Integer, String> STATUS_CODES = new HashMap<Integer, String>() {{
		put(SC_CONTINUE, "Continue");
		put(SC_OK, "OK");
		put(SC_MOVED_TEMPORARILY, "Moved Temporarily");
		put(SC_NOT_MODIFIED, "Not Modified");
		put(SC_BAD_REQUEST, "Bad Request");
		put(SC_FORBIDDEN, "Forbidden");
		put(SC_NOT_FOUND, "Not Found");
		put(SC_PRECONDITION_FAILED, "Precondition Failed");
		put(SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
		put(SC_NOT_IMPLEMENTED, "Not Implemented");
	}}; 
	
	private String httpVersion;
	private String contentType;
	private Locale locale;
	private ByteArrayOutputStream outputWrapper;
	private DataOutputStream outputStream;
	private String path;
	private int serverPort;
	private String hostName;
	private boolean committed;

	private static DateFormat headerDateFormat;
	private static DateFormat cookieDateFormat;
	static {
		headerDateFormat = new SimpleDateFormat("EEE, d MMM yyyy H:mm:ss z");
		headerDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));		
		cookieDateFormat = new SimpleDateFormat("EEE, d-MMM-yyyy HH:mm:ss z");
		cookieDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public HttpServletResponseSupport(String httpVersion, DataOutputStream outputStream) {
		this.httpVersion = httpVersion;
		this.outputStream = outputStream;
		outputWrapper = new ByteArrayOutputStream();
	}
	
	public void addCookie(Cookie cookie) {
		try {
			if (cookie.getMaxAge() == -1) {
				outputStream.writeBytes("Set-Cookie: " + cookie.getName() + "=" + 
					cookie.getValue() + LINE_SEPARATOR);
			} else {
				Calendar expiry = GregorianCalendar.getInstance();
				expiry.add(Calendar.SECOND, cookie.getMaxAge() - 1); // 1 second delay
				outputStream.writeBytes("Set-Cookie: " + cookie.getName() + "=" + cookie.getValue() +
					"; expires=" + cookieDateFormat.format(expiry.getTime()) + LINE_SEPARATOR);
			}
		} catch (IOException ignored) { }
	}

	public void addDateHeader(String name, long value) {
		addHeader(name, String.valueOf(value));
	}

	public void addHeader(String name, String value) {
		try {
			outputStream.writeBytes(name + ": " + value + LINE_SEPARATOR);
		} catch (IOException ignored) { }
	}

	public void addIntHeader(String name, int value) {
		addHeader(name, String.valueOf(value));
	}

	public boolean containsHeader(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public String encodeRedirectURL(String url) {
		return url;
	}

	@Deprecated
	public String encodeRedirectUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String encodeURL(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Deprecated
	public String encodeUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendError(int arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	public void sendError(int arg0, String arg1) throws IOException {
		// TODO Auto-generated method stub

	}

	public void sendRedirect(String location) throws IOException {
		if (isCommitted()) {
			throw new IllegalStateException("Response already committed");
		}
		setStatus(SC_MOVED_TEMPORARILY);
		String redirect = "http://" + hostName + ":" + serverPort;
		if (location != null) { 
			if (location != null && location.startsWith("/")) {
				redirect += location;
			} else {
				redirect += path.substring(0, path.lastIndexOf("/") + 1) + location;
			}
		}
		addHeader("Location", redirect);
        committed = true;
	}

	public void setDateHeader(String name, long date) {
		try {
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTimeInMillis(date);
			outputStream.writeBytes("Date: " + headerDateFormat.format(calendar.getTime()) + LINE_SEPARATOR);
		} catch (IOException ignored) { }
	}

	public void setHeader(String name, String value) {
		try {
			outputStream.writeBytes(name + ": " + value + LINE_SEPARATOR);
		} catch (IOException ignored) { }
	}

	public void setIntHeader(String name, int value) {
		setHeader(name, String.valueOf(value)); 
	}

	public void setStatus(int sc) {
		try {
			outputStream.writeBytes(getStatusLine(httpVersion, sc));
		} catch (IOException ignored) { }
	}

	@Deprecated
	public void setStatus(int sc, String sm) {
		try {
			outputStream.writeBytes(httpVersion + " " + sc + " " + sm + LINE_SEPARATOR);
		} catch (IOException ignored) { }
	}

	public void flushBuffer() throws IOException {
		outputStream.flush();
	}

	public int getBufferSize() {
		return outputStream.size();
	}

	public String getCharacterEncoding() {
		return "ISO-8859-1";
	}

	public String getContentType() {
		return contentType == null ? DEFAULT_CONTENT_TYPE : contentType;
	}

	public Locale getLocale() {
		return locale;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException("not implemented");
	}

	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(outputWrapper, true);
	}

	public boolean isCommitted() {
		return committed;
	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public void resetBuffer() {
		// TODO Auto-generated method stub

	}

	public void setBufferSize(int arg0) {
		// TODO Auto-generated method stub

	}

	public void setCharacterEncoding(String encoding) {
		throw new UnsupportedOperationException("no op");
	}

	public void setContentLength(int arg0) {
		// TODO Auto-generated method stub

	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
//		try {
//			outputStream.writeBytes("Content-Type: " + getContentType() + LINE_SEPARATOR);
//		} catch (IOException ignored) { }
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	ByteArrayOutputStream getWriterWrapper() {
		return outputWrapper;
	}
	
	public static String getStatusLine(String version, int sc) {
		return version + " " + sc + " " + STATUS_CODES.get(sc) + LINE_SEPARATOR;
	}
	
	void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	void setPath(String path) {
		this.path = path;
	}
	
	void setCommitted(boolean committed) {
		this.committed = committed;
	}
}
