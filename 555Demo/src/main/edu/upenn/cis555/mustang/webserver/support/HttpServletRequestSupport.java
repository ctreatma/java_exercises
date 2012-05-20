package edu.upenn.cis555.mustang.webserver.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpServletRequestSupport implements HttpServletRequest {
	private String characterEncoding;
	private HttpSessionSupport session;
	private Map<String, List<String>> params = new ConcurrentHashMap<String, List<String>>();
	private Properties props = new Properties();
	private String method;
	private List<Cookie> cookies = new ArrayList<Cookie>();
	private String path;
	private Properties headers = new Properties();
	private BufferedReader reader;
	private int port;
	
	public HttpServletRequestSupport(HttpSessionSupport session) {
		this.session = session;
	}
	
	public String getAuthType() {
		return BASIC_AUTH;
	}

	public String getContextPath() {
		return "";	// support default context path only
	}

	public Cookie[] getCookies() {
		return cookies.toArray(new Cookie[cookies.size()]);
	}

	public long getDateHeader(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		
		Date value;
		DateFormat dateFormat = new SimpleDateFormat("EEE MMM d H:mm:ss yyyy");
		try {
			value = dateFormat.parse(name.toLowerCase());
			return value.getTime();
		} catch (ParseException e) { }
		dateFormat = new SimpleDateFormat("EEE, d MMM yyyy H:mm:ss z");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			value = dateFormat.parse(name.toLowerCase());
			return value.getTime();
		} catch (ParseException e) { }
		dateFormat = new SimpleDateFormat("EEEE, d-MMM-yy H:mm:ss z");	
		try {
			value = dateFormat.parse(name.toLowerCase());
			return value.getTime();
		} catch (ParseException e) { 
			throw new IllegalArgumentException(name + " not a valid date header");
		}
	}

	public String getHeader(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		
		return (String) headers.get(name.toLowerCase());
	}

	public Enumeration<String> getHeaderNames() {
		Vector<String> names = new Vector<String>();
		Enumeration<Object> keys = headers.keys();
		while (keys.hasMoreElements()) {
			names.add((String) keys.nextElement());
		}
		return names.elements();
	}

	public Enumeration<String> getHeaders(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		
		Vector<String> values = new Vector<String>();
		String[] tokens = ((String) headers.get(name.toLowerCase())).split(",");
		for (String token : tokens) {
			values.add(token.trim());
		}
		return values.elements();
	}

	public int getIntHeader(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		
		int value;
		try {
			value = Integer.parseInt((String) headers.get(name.toLowerCase()));
		} catch (NumberFormatException e) {
			throw e;
		}
		return value;
	}

	public String getMethod() {
		return method;
	}

	public String getPathInfo() {
		String extra = null;
		if (path != null) {
			 extra = path.replace(getContextPath(), "");
			 if (extra.indexOf("/", 1) > -1) {
				 extra = extra.substring(extra.indexOf("/", 1));
			 } else {
				 extra = null;
			 }
		}
		return extra;
	}

	public String getPathTranslated() {
		throw new UnsupportedOperationException("not implemented");
	}

	public String getQueryString() {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, List<String>> entry : params.entrySet()) {
			for (String value : entry.getValue()) {
				builder.append("&").append(entry.getKey()).append("=").append(value);
			}
		}
		return builder.toString().replaceFirst("&", "");
	}

	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRequestURI() {
		return path;
	}

	public StringBuffer getRequestURL() {
		StringBuffer url = new StringBuffer();
		url.append(getProtocol()).append("://").append(getServerName()).append(":")
			.append(getServerPort()).append(getRequestURI());
		return url;
	}

	public String getRequestedSessionId() {
		HttpSession session = getSession(false);
		if (session == null) {
			return null;
		}
		
		return session.getId();
	}

	public String getServletPath() {
		String servletPath = null;
		if (path != null) {
			servletPath = path.replace(getContextPath(), "");
			if (servletPath.indexOf("/", 1) > -1) {
				servletPath = servletPath.substring(0, servletPath.indexOf("/", 1));
			}
		}
		return servletPath;
	}

	public HttpSession getSession() {
		return getSession(true);
	}

	public HttpSession getSession(boolean createNew) {
		if (!hasSession()) {
			session = createNew ? new HttpSessionSupport() : null;
		}
		return session;
	}

	public Principal getUserPrincipal() {
		throw new UnsupportedOperationException("not implemented");
	}

	public boolean isRequestedSessionIdFromCookie() {
		// support cookie-based session only
		return true;
	}

	public boolean isRequestedSessionIdFromURL() {
		// not support URL rewrite session
		return false;
	}

	public boolean isRequestedSessionIdFromUrl() {
		// not support URL rewrite session
		return false;
	}

	public boolean isRequestedSessionIdValid() {
		return getRequestedSessionId() != null;
	}

	public boolean isUserInRole(String arg0) {
		throw new UnsupportedOperationException("not implemented");
	}

	public Object getAttribute(String name) {
		return props.get(name);
	}

	public Enumeration<String> getAttributeNames() {
		Vector<String> attribs = new Vector<String>();
		Enumeration<Object> keys = props.keys();
		while (keys.hasMoreElements()) {
			attribs.add(keys.nextElement().toString());
		}
		return attribs.elements();
	}

	public String getCharacterEncoding() {
		return characterEncoding == null ? "ISO-8859-1" : characterEncoding;
	}

	public int getContentLength() {
		String value = getHeader("Content-Length");
		try {
			int contentLength = Integer.parseInt(value);
			return contentLength;
		} catch (NumberFormatException e) {
			throw e;
		}
	}

	public String getContentType() {
		return getHeader("Content-Type");
	}

	public ServletInputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException("not implemented");
	}

	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Locale getLocale() {
		Locale locale = null;
		String localeCode = getHeader("Accept-Language");
		if (localeCode != null) {
			String[] codes = localeCode.split("\\-|_");
			if (codes.length > 1) {
				locale = new Locale(codes[0], codes[1].toUpperCase());
			} else if (codes.length == 1) {
				locale = new Locale(codes[0]);
			} else {
				locale = new Locale("");
			}
		}
		return locale;
	}

	public Enumeration<Locale> getLocales() {
		throw new UnsupportedOperationException("not implemented");
	}

	public String getParameter(String name) {
		if (params.get(name) == null || params.get(name).isEmpty()) {
			return null;
		}
		return params.get(name).get(0);
	}

	public Map<String, List<String>> getParameterMap() {
		return params;
	}

	public Enumeration<String> getParameterNames() {
		Vector<String> names = new Vector<String>();
		Set<String> keys = params.keySet();
		for (String key : keys) {
			names.add(key);
		}
		return names.elements();
	}

	public String[] getParameterValues(String name) {
		if (params.get(name) == null) {
			return null;
		}
		return params.get(name).toArray(new String[params.get(name).size()]);
	}

	public String getProtocol() {
		return "http";
	}

	public BufferedReader getReader() throws IOException {
		return reader;
	}

	@Deprecated
	public String getRealPath(String arg0) {
		return null;
	}

	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		throw new UnsupportedOperationException("not implemented");
	}

	public String getScheme() {
		return "http";
	}

	public String getServerName() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			return localHost.getHostName();
		} catch (UnknownHostException e) {
			return "Unknown";
        }
	}

	public int getServerPort() {
		return port;
	}

	public boolean isSecure() {
		return "https".equalsIgnoreCase(getProtocol());
	}

	public void removeAttribute(String name) {
		props.remove(name);
	}

	public void setAttribute(String name, Object value) {
		props.put(name, value);
	}

	public void setCharacterEncoding(String characterEncoding) 
		throws UnsupportedEncodingException {
		this.characterEncoding = characterEncoding;
	}

	void setMethod(String method) {
		this.method = method;
	}
	
	void setParameter(String name, String value) {
		List<String> values = params.get(name);
		if (values == null) {
			 values = new ArrayList<String>();
			 params.put(name, values);	 
		}
		values.add(value);
	}
	
	void clearParameters() {
		params.clear();
	}
	
	void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}
	
	void addHeader(String name, String value) {
		headers.put(name.toLowerCase(), value); // made case-insensitive
	}

	void setPath(String path) {
		this.path = path;
	}
	
	void setReader(BufferedReader reader) {
		this.reader = reader;
	}
	
	void setServerPort(int port) {
		this.port = port;
	}
	
	private boolean hasSession() {
		return session != null && session.isValid();
	}
}
