package edu.upenn.cis555.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HttpServletResponseImpl implements HttpServletResponse {
    private List<Cookie> cookies = new ArrayList<Cookie>();
    private HashMap<String,ArrayList<String>> headers = new HashMap<String,ArrayList<String>>();
    private boolean isCommitted = false;
    private String contentType = "text/html";
    private ResponseWriter writer;
    private Locale locale;
    private int contentLength;
    private int status = HttpServletResponse.SC_OK;
    private String encoding = "ISO-8859-1";
    private OutputStream output;
    private HttpServletRequestImpl request;
    private int bufferSize;
    private SimpleDateFormat format;
    
    public HttpServletResponseImpl(OutputStream output, HttpServletRequestImpl request) {
        this.format = new SimpleDateFormat("EEE, dd-MMM-yy HH:mm:ss z");
        this.output = output;
        this.request = request;
    }
    
    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public void addDateHeader(String name, long value) {
        ArrayList<String> headerValues;
        if (headers.containsKey(name)) {
            headerValues = headers.get(name);
        }
        else {
            headerValues = new ArrayList<String>();
        }
        headerValues.add(format.format(new Date(value)));
        headers.put(name, headerValues);
    }

    @Override
    public void addHeader(String name, String value) {
        ArrayList<String> headerValues;
        if (headers.containsKey(name)) {
            headerValues = headers.get(name);
        }
        else {
            headerValues = new ArrayList<String>();
        }
        headerValues.add(value);
        headers.put(name, headerValues);
    }

    @Override
    public void addIntHeader(String name, int value) {
        ArrayList<String> headerValues;
        if (headers.containsKey(name)) {
            headerValues = headers.get(name);
        }
        else {
            headerValues = new ArrayList<String>();
        }
        headerValues.add(Integer.toString(value));
        headers.put(name, headerValues);
    }

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return url;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        // Deprecated
        return null;
    }

    @Override
    public String encodeURL(String url) {
        return url;
    }

    @Override
    public String encodeUrl(String url) {
        // Deprecated
        return null;
    }

    @Override
    public void sendError(int status) throws IOException {
        if (isCommitted) {
            throw new IllegalStateException("Attempted to send an error for a response that is already committed.");
        }
        this.status = status;
        getWriter().flush();
    }

    @Override
    public void sendError(int status, String message) throws IOException {
        if (isCommitted) {
            throw new IllegalStateException("Attempted to send an error for a response that is already committed.");
        }
        this.status = status;
        PrintWriter w = getWriter();
        w.println("<html><head><title>" + message + "</title></head><body>" + message + "</body></html>");
    }

    @Override
    public void sendRedirect(String url) throws IOException {
        // Need to check for relative url, and turn into absolute url
        status = HttpServletResponse.SC_FOUND;
        this.setHeader("Location", url);
        contentLength = 0;
    }

    @Override
    public void setDateHeader(String name, long value) {
        ArrayList<String> headerValues = new ArrayList<String>();
        headerValues.add(format.format(new Date(value)));
        headers.put(name, headerValues);
    }

    @Override
    public void setHeader(String name, String value) {
        ArrayList<String> headerValues = new ArrayList<String>();
        headerValues.add(value);
        headers.put(name, headerValues);
    }

    @Override
    public void setIntHeader(String name, int value) {
        ArrayList<String> headerValues = new ArrayList<String>();
        headerValues.add(Integer.toString(value));
        headers.put(name, headerValues);
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void setStatus(int arg0, String arg1) {
        // Deprecated
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        }
    }

    @Override
    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public String getCharacterEncoding() {
        return encoding;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // Not implemented per assignment directions
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new ResponseWriter(this, new OutputStreamWriter(output, encoding));
        }
        return writer;
    }

    @Override
    public boolean isCommitted() {
        return isCommitted;
    }

    @Override
    public void reset() {
        if (isCommitted) {
            throw new IllegalStateException("Attempted to reset a response that is already committed.");
        }
        cookies = new ArrayList<Cookie>();
        headers = new HashMap<String,ArrayList<String>>();
        contentType = "text/html";
        writer = null;
        locale = null;
        contentLength = 0;
        status = SC_OK;
        encoding = "ISO-8859-1";
    }

    @Override
    public void resetBuffer() {
        if (isCommitted) {
            throw new IllegalStateException("Attempted to reset a response that is already committed.");
        }
        // Not really using a buffer...
    }

    @Override
    public void setBufferSize(int bufferSize) {
        // Not using a buffer, so nothing to set size on.
    }

    @Override
    public void setCharacterEncoding(String encoding) {
        // Do nothing.
    }

    @Override
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    int getStatus() {
        return status;
    }

    void setCommitted(boolean isCommitted) {
        this.isCommitted = isCommitted;
    }

    List<Cookie> getCookies() {
        return cookies;
    }

    HashMap<String,ArrayList<String>> getHeaders() {
        return headers;
    }
    
    HttpServletRequestImpl getRequest() {
        return request;
    }
    
    int getContentLength() {
        return contentLength;
    }
    
    void commit() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        StringBuffer headerContent = new StringBuffer();
        headerContent.append(request.getProtocol() + " " + getStatus() + " " + HttpReason.get(getStatus()) + "\r\n");
        headerContent.append("Date: " + format.format(new Date()) + "\r\n");
        for (Object name : getHeaders().keySet()) {
            ArrayList<String> headerValues = getHeaders().get(name);
            String valuesString = headerValues.get(0);
            for (int i = 1; i < headerValues.size(); ++i) {
                valuesString += ", " + headerValues.get(i);
            }
            headerContent.append(name + ": " + valuesString + "\r\n");
        }
        if (getCookies().size() > 0 || (getRequest().hasSession()
                && getRequest().getSession().isNew())) {
            headerContent.append("Cache-control: no-cache=\"set-cookie\"\r\n");
            headerContent.append("Expires: Sat, 26 Jul 1997 05:00:00 GMT\r\n");
            for (Cookie cookie : getCookies()) {
                headerContent.append("Set-Cookie: ");
                headerContent.append(cookieHeader(cookie));
                headerContent.append("\r\n");
            }
            if (getRequest().hasSession() && getRequest().getSession().isNew()) {
                HttpSession session = getRequest().getSession();
                Cookie cookie = new Cookie("httpSessionId", session.getId());
                headerContent.append("Set-Cookie: ");
                headerContent.append(cookieHeader(cookie));
                headerContent.append("\r\n");            
            }
        }
        headerContent.append("Content-Length: " + getContentLength() + "\r\n");
        headerContent.append("Connection: close\r\n\r\n");

        output.write(headerContent.toString().getBytes());
        output.flush();
        
        isCommitted = true;
    }
    
    private String cookieHeader(Cookie cookie) {
        StringBuffer header = new StringBuffer();
        
        header.append(cookie.getName());
        header.append("=");
        header.append(cookie.getValue());
        if (cookie.getComment() != null) {
            header.append("; Comment=");
            header.append(cookie.getComment());
        }
        if (cookie.getDomain() != null) {
            header.append("; Domain=");
            header.append(cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            header.append("; Path=");
            header.append(cookie.getPath());
        }
        header.append("; Version=");
        header.append(cookie.getVersion());
        if (cookie.getMaxAge() >= 0) {
            if (cookie.getVersion() == 0) {
                long expires = 1000 * cookie.getMaxAge();
                expires += new Date().getTime();
                header.append("; Expires=");
                header.append(format.format(new Date(expires)));
            }
            else {
                header.append("; Max-Age=");
                header.append(cookie.getMaxAge());
            }
        }
        if (cookie.getSecure()) {
            header.append("; Secure");
        }

        return header.toString();
    }
}
