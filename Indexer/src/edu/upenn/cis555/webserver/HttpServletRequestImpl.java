package edu.upenn.cis555.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpServletRequestImpl implements HttpServletRequest {     
    private HashMap<String,ArrayList<String>> params = new HashMap<String,ArrayList<String>>();
    private HashMap<String,Object> props = new HashMap<String,Object>();
    private HashMap<String,ArrayList<String>> headers = new HashMap<String,ArrayList<String>>();
    private List<Cookie> cookies = new ArrayList<Cookie>();
    private HttpSessionImpl session = null;
    private String method;
    private String encoding = "ISO-8859-1";
    private boolean secure = false;
    private String queryString;
    private String contentType;
    private Locale locale;
    private String pathInfo = "";
    private String servletPath;
    private String protocol;
    private int remotePort;
    private String remoteAddr;
    private String remoteHost;
    private InputStream input;
    private BufferedReader reader;
    private String localHost;
    private String localAddr;
    private int localPort;

    public HttpServletRequestImpl() {
    }
    
    @Override
    public String getAuthType() {
        return "BASIC";
    }

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public Cookie[] getCookies() {
        return cookies.toArray(new Cookie[cookies.size()]);
    }

    @Override
    public long getDateHeader(String name) {
        name = name.toLowerCase();
        if (headers.containsKey(name)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                SimpleDateFormat format2 = new SimpleDateFormat("EEEE, d-MMM-yy HH:mm:ss z");
                format2.setTimeZone(TimeZone.getTimeZone("GMT"));
                SimpleDateFormat format3 = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
                format3.setTimeZone(TimeZone.getTimeZone("GMT"));
                String headerValue = headers.get(name).get(0);
                Date value;
                try {
                    value = format.parse(headerValue); 
                }
                catch(ParseException ex) {
                    try {
                        value = format2.parse(headerValue); 
                    }
                    catch(ParseException ex2) {
                        value = format3.parse(headerValue); 
                    }
                }
                return value.getTime();
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Header could not be converted to Date.");
            }
            
        }
        return -1;
    }

    @Override
    public String getHeader(String name) {
        name = name.toLowerCase();
        if (headers.containsKey(name)) {
            return headers.get(name).get(0);
        }
        return null;
    }

    @Override
    public Enumeration getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    @Override
    public Enumeration getHeaders(String arg0) {
        arg0 = arg0.toLowerCase();
        if (headers.containsKey(arg0)) {
            return Collections.enumeration(headers.get(arg0));
        }
        return Collections.enumeration(Collections.EMPTY_SET);
    }

    @Override
    public int getIntHeader(String name) {
        name = name.toLowerCase();
        if (headers.containsKey(name)) {
            return Integer.parseInt(headers.get(name).get(0));
        }
        return -1;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getPathInfo() {
        return pathInfo;
    }

    @Override
    public String getPathTranslated() {
        // Not implemented per assignment directions
        return null;
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    @Override
    public String getRemoteUser() {
        // Don't have a remote user
        return null;
    }

    @Override
    public String getRequestURI() {
        return this.getContextPath() + this.getServletPath() + this.getPathInfo();
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer url = new StringBuffer();
        url.append(this.getScheme());
        url.append("://");
        url.append(this.getServerName());
        url.append(":");
        url.append(this.getServerPort());
        url.append(this.getRequestURI());
        return url;
    }

    @Override
    public String getRequestedSessionId() {
        if (hasSession()) {
            return session.getId();
        }
        return null;
    }

    @Override
    public String getServletPath() {
        return servletPath;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (create) {
            if (!hasSession()) {
                session = new HttpSessionImpl();
            }
        } else {
            if (!hasSession()) {
                session = null;
            }
        }
        return session;
    }

    @Override
    public Principal getUserPrincipal() {
        // Not implemented per assignment directions
        return null;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        // If this request has a valid, old session then
        // the id came from a cookie
        return hasSession() && !session.isNew();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        // Deprecated
        return false;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return hasSession();
    }

    @Override
    public boolean isUserInRole(String arg0) {
        // Not implemented per assignment directions
        return false;
    }

    @Override
    public Object getAttribute(String arg0) {
        return props.get(arg0);
    }

    @Override
    public Enumeration getAttributeNames() {
        return Collections.enumeration(props.keySet());
    }

    @Override
    public String getCharacterEncoding() {
        return encoding;
    }

    @Override
    public int getContentLength() {
        return -1;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // Not implemented per assignment directions
        return null;
    }

    @Override
    public String getLocalAddr() {
        return localAddr;
    }

    @Override
    public String getLocalName() {
        return localHost;
    }

    @Override
    public int getLocalPort() {
        return localPort;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public Enumeration getLocales() {
        // Not implemented per assignment directions
        return null;
    }

    @Override
    public String getParameter(String key) {
        if (params.containsKey(key)) {
            return params.get(key).get(0);
        }
        return null;
    }

    @Override
    public Map getParameterMap() {
        return params;
    }

    @Override
    public Enumeration getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        if (params.containsKey(name)) {
            ArrayList<String> values = params.get(name);
            return values.toArray(new String[values.size()]);
        }
        return null;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(input, encoding));
            while (reader.readLine() != null && reader.readLine().trim().length() != 0) {
                // Skip over the headers to the request body
            }
        }
        return reader;
    }

    @Override
    public String getRealPath(String arg0) {
        // Deprecated
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    @Override
    public int getRemotePort() {
        return remotePort;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String arg0) {
        // Not implemented per assignment directions
        return null;
    }

    @Override
    public String getScheme() {
        return "http";
    }

    @Override
    public String getServerName() {
        return localHost;
    }

    @Override
    public int getServerPort() {
        return localPort;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public void removeAttribute(String name) {
        props.remove(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        props.put(name, value);
    }

    @Override
    public void setCharacterEncoding(String encoding)
            throws UnsupportedEncodingException {
        this.encoding = encoding;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public void addParameter(String key, String value) {
        try {
            key = URLDecoder.decode(key, encoding);
            value = URLDecoder.decode(value, encoding);
        }
        catch (UnsupportedEncodingException ex) {
            // Ignore;
        }
        ArrayList<String> values;
        if (params.containsKey(key)) {
            values = params.get(key);
        }
        else {
            values = new ArrayList<String>();
        }
        values.add(value);
        params.put(key, values);
    }
    
    void clearParameters() {
        params.clear();
    }
    
    boolean hasSession() {
        return ((session != null) && session.isValid());
    }

    void setSession(HttpSessionImpl session) {
        this.session = session;
    }

    void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    void setHeaders(HashMap<String, ArrayList<String>> headers) {
        this.headers.clear();
        for (String key : headers.keySet()) {
            this.headers.put(key.toLowerCase(), headers.get(key));
        }
    }

    void setQueryString(String queryString) {
        this.queryString = queryString;       
    }

    void setPathInfo(String pathInfo) {
        if (pathInfo != null) {
            this.pathInfo = pathInfo;
        }
    }

    void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }
    
    void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    void setInputStream(InputStream input) {
        this.input = input;
    }

    void setServerHost(String localHost) {
        this.localHost = localHost;
    }

    void setServerAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    void setServerPort(int localPort) {
        this.localPort = localPort;
    }
}
