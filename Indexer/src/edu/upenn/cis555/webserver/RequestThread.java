package edu.upenn.cis555.webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;

public class RequestThread extends Thread {
    private ThreadPool owner;
    private RequestQueue requests;
    
    public RequestThread(ThreadPool owner, RequestQueue requests) {
        this.owner = owner;
        this.requests = requests;
    }

    public void run() {
        while (true) {
            Socket socket = requests.getRequest();
            openSocket(socket);
        }
    }

    private void openSocket(Socket socket) {
        owner.startRequest();
        InputStream input;
        OutputStream output;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
            handleRequest(input, output, socket);
        }
        catch (IOException ex) {
            Httpserver.logError(ex, "Thread '" + this.getName() + "' had an error opening the socket streams.");
        }
        finally {
            try {
                socket.close();
            }
            catch (IOException ex) {
                Httpserver.logError(ex, "Thread '" + this.getName() + "' had an error closing the socket.");
            }
            owner.finishRequest();
        }
    }

    private void handleRequest(InputStream input, OutputStream output, Socket socket) {
        Date modifiedSince = null;
        Date unmodifiedSince = null;
        String params = null;
        List<Cookie> cookies = new ArrayList<Cookie>();
        HashMap<String,ArrayList<String>> headers = new HashMap<String, ArrayList<String>>();
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat format2 = new SimpleDateFormat("EEEE, d-MMM-yy HH:mm:ss z");
        format2.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat format3 = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
        format3.setTimeZone(TimeZone.getTimeZone("GMT"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String responseContent;
        try {
            String request = reader.readLine();
            String[] requestParts = request.split("\\s");
            if (requestParts.length != 3 || !(requestParts[2].equals("HTTP/1.0") || requestParts[2].equals("HTTP/1.1"))) {
                // Not sure what to do in this case?
                responseContent = "<html><body><h2>400 Bad Request</h2><p>I have no idea what you're trying to do.</p></body></html>";

                output.write(("HTTP/1.1 400 Bad Request\r\n").getBytes());
                output.write("Content-Type: text/html\r\n".getBytes());
                output.write(("Content-Length: " + responseContent.getBytes().length + "\r\n").getBytes());
                output.write(("Connection: close\r\n\r\n").getBytes());
                output.write(responseContent.getBytes());
            }
            else {
                if (!(requestParts[0].equals("GET") || requestParts[0].equals("POST") || requestParts[0].equals("HEAD"))) {
                    responseContent = "<html><body><h2>501 Not Implemented</h2><p>The requested method is not implemented on this server.</p></body></html>";

                    output.write((requestParts[2] + " 501 Not Implemented\r\n").getBytes());
                    output.write(("Date: " + format.format(new Date()) + "\r\n").getBytes());
                    output.write("Content-Type: text/html\r\n".getBytes());
                    output.write(("Content-Length: " + responseContent.getBytes().length + "\r\n").getBytes());
                    output.write(("Connection: close\r\n\r\n").getBytes());
                    output.write(responseContent.getBytes());
                }
                else {
                    if (requestParts[2].equals("HTTP/1.1")) {
                        output.write((requestParts[2] + " 100 Continue\r\n\r\n").getBytes());
                        output.flush();
                    }
                    ArrayList<String> headerLines = new ArrayList<String>(0);
                    boolean headersDone = false;
                    boolean includesHost = false;
                    while (!headersDone) {
                        String header = reader.readLine();
                        if (header.trim().length() == 0) {
                            headersDone = true;
                        }
                        else {
                            if (headerLines.size() > 0 && (header.startsWith(" ") || header.startsWith("\t"))) {
                                header = headerLines.remove(headerLines.size()-1) + header;   
                            }
                            headerLines.add(header);
                        }
                    }
                    if (requestParts[2].equals("HTTP/1.1")) {
                        for (int i = 0; i < headerLines.size(); ++i) {
                            String header = headerLines.get(i);
                            String headerName = header.substring(0, header.indexOf(":")).trim();
                            String headerValue = header.substring(header.indexOf(":") + 1).trim();
                            if (headerName.equalsIgnoreCase("Host")) {
                                includesHost = true;
                            }
                            else if (headerName.equalsIgnoreCase("If-Modified-Since")) {
                                try {
                                    modifiedSince = format.parse(headerValue); 
                                }
                                catch(ParseException ex) {
                                    try {
                                        modifiedSince = format2.parse(headerValue); 
                                    }
                                    catch(ParseException ex2) {
                                        try {
                                            modifiedSince = format3.parse(headerValue); 
                                        }
                                        catch(ParseException ex3) {
                                            // Couldn't parse date...so just ignore the header?
                                            Httpserver.logError(ex3, null);
                                        }
                                    }
                                }
                            }
                            else if (headerName.equalsIgnoreCase("If-Unmodified-Since")) {
                                try {
                                    unmodifiedSince = format.parse(headerValue); 
                                }
                                catch(ParseException ex) {
                                    try {
                                        unmodifiedSince = format2.parse(headerValue);
                                    }
                                    catch(ParseException ex2) {
                                        try {
                                            unmodifiedSince = format3.parse(headerValue);
                                        }
                                        catch(ParseException ex3) {
                                            // Couldn't parse date...so just ignore the header?
                                            Httpserver.logError(ex3, null);
                                        } 
                                    } 
                                }                         
                            }
                            else if (requestParts[0].equals("POST") && headerName.equalsIgnoreCase("Content-length")) {
                                int contentLength = Integer.valueOf(headerValue);
                                char[] contentChars = new char[contentLength];
                                reader.read(contentChars, 0, contentLength);
                                params = new String(contentChars);
                            }
                            else if (headerName.equalsIgnoreCase("Cookie")) {
                                String[] cookieStrings = headerValue.split("[;,]");
                                int version = 0;
                                Cookie current = null;
                                for (String cookie : cookieStrings) {
                                    String[] nameValuePair = cookie.split("=");
                                    if (nameValuePair[0].equalsIgnoreCase("$Domain")) {
                                        current.setDomain(nameValuePair[1]);
                                    }
                                    if (nameValuePair[0].equalsIgnoreCase("$Path")) {
                                        current.setPath(nameValuePair[1]);
                                    }
                                    if (nameValuePair[0].equalsIgnoreCase("$Version")) {
                                        version = Integer.parseInt(nameValuePair[1]);
                                    }
                                    else {
                                        current = new Cookie(nameValuePair[0].trim(), nameValuePair[1].trim());
                                        current.setVersion(version);
                                        cookies.add(current);
                                    }
                                }
                            }
                            headerName = headerName.toLowerCase();
                            ArrayList<String> headerValues;
                            if (headers.containsKey(headerName)) {
                                headerValues = headers.get(headerName);
                            }
                            else {
                                headerValues = new ArrayList<String>();
                            }
                            headerValues.add(headerValue);
                            headers.put(headerName, headerValues);
                        }
                    }
                    if (requestParts[2].equals("HTTP/1.1") && !includesHost) {
                        responseContent = "<html><body><h2>400 Bad Request</h2><p>No Host: header received</p><p>HTTP 1.1 requests must include the Host: header.</p></body></html>";

                        output.write((requestParts[2] + " 400 Bad Request\r\n").getBytes());
                        output.write(("Date: " + format.format(new Date()) + "\r\n").getBytes());
                        output.write("Content-Type: text/html\r\n".getBytes());
                        output.write(("Content-Length: " + responseContent.getBytes().length + "\r\n").getBytes());
                        output.write(("Connection: close\r\n\r\n").getBytes());
                        output.write(responseContent.getBytes());
                    }
                    else {
                        String requestPath = requestParts[1];
                        Pattern pattern = Pattern.compile("https?://[^/]*(/.*)");
                        Matcher matcher = pattern.matcher(requestPath);
                        if (matcher.matches()) {
                            requestPath = matcher.group(1);
                        }
                        pattern = Pattern.compile("([^\\?]*)\\??(.*)");
                        matcher = pattern.matcher(requestPath);
                        if (matcher.matches()) {
                            requestPath = matcher.group(1);
                            if (requestParts[0].equals("GET")) {
                                params = matcher.group(2);
                            }
                        }
                        requestPath = URLDecoder.decode(requestPath, "UTF-8");
                        boolean isServletRequest = false;
                        String servletPath = null;
                        String pathInfo = null;
                        HttpServlet servlet = null;
                        for (String urlPattern : Httpserver.h.urlPatterns.keySet()) {
                            boolean hasPathInfo = urlPattern.endsWith("*");
                            String urlRegexp;
                            if (hasPathInfo) {
                                String prefix = urlPattern.substring(0, urlPattern.indexOf("*"));
                                urlRegexp = "(" + prefix + ")" + "(/.*)";
                            }
                            else {
                                urlRegexp = "(" + urlPattern + ")$";
                            }
                            pattern = Pattern.compile(urlRegexp);
                            matcher = pattern.matcher(requestPath);
                            if (matcher.matches()) {
                                isServletRequest = true;
                                servletPath = matcher.group(1);
                                if (hasPathInfo) {
                                    pathInfo = matcher.group(2);
                                }
                                if (Httpserver.servlets != null) {
                                    servlet = Httpserver.servlets.get(Httpserver.h.urlPatterns.get(urlPattern));
                                }
                                break;
                            }
                        }
                        if (isServletRequest) {
                            if (servlet != null) {
                                HttpServletRequestImpl servletRequest = new HttpServletRequestImpl();
                                HttpServletResponseImpl servletResponse = new HttpServletResponseImpl(output, servletRequest);
                                
                                servletRequest.setInputStream(input);
                                
                                servletRequest.setServerHost(socket.getLocalAddress().getHostName());
                                servletRequest.setServerAddr(socket.getLocalAddress().getHostAddress());
                                servletRequest.setServerPort(socket.getLocalPort()); 
                                
                                servletRequest.setRemoteHost(socket.getInetAddress().getHostName());
                                servletRequest.setRemoteAddr(socket.getInetAddress().getHostAddress());
                                servletRequest.setRemotePort(socket.getPort());
                                
                                servletRequest.setProtocol(requestParts[2]);
                                servletRequest.setServletPath(servletPath);
                                servletRequest.setPathInfo(pathInfo);
                                if (params != null && params.trim().length() > 0) {
                                    if (requestParts[0].equals("GET")) {
                                        servletRequest.setQueryString("?" + params);
                                    }
                                    String[] paramsArray = params.split("&");
                                    for (String param : paramsArray) {
                                        String[] paramParts = param.split("=");
                                        servletRequest.addParameter(paramParts[0], paramParts[1]);
                                    }
                                }
                                servletRequest.setMethod(requestParts[0]);
                                servletRequest.setHeaders(headers);
                                for (Cookie cookie : cookies) {
                                    servletRequest.addCookie(cookie);
                                    if (cookie.getName().equalsIgnoreCase("httpSessionId")) {
                                        HttpSessionImpl session = Httpserver.context.getSession(cookie.getValue());
                                        if (session != null) {
                                            session.setNew(false);
                                            servletRequest.setSession(session);
                                        }
                                    }
                                }
                                try {
                                    servlet.service(servletRequest, servletResponse);
                                    servletResponse.getWriter().flush();
                                }
                                catch (Exception ex) {
                                    responseContent = "<html><body><h2>500 Internal Server Error</h2><p>An unexpected error occurred.</p></body></html>";

                                    output.write((requestParts[2] + " 500 Internal Server Error\r\n").getBytes());
                                    output.write(("Date: " + format.format(new Date()) + "\r\n").getBytes());
                                    output.write("Content-Type: text/html\r\n".getBytes());
                                    output.write(("Content-Length: " + responseContent.getBytes().length + "\r\n").getBytes());
                                    output.write(("Connection: close\r\n\r\n").getBytes());
                                    output.write(responseContent.getBytes());
                                    Httpserver.logError(ex, null);
                                }
                            }
                            else {
                                responseContent = "<html><body><h2>503 Service Unavailable</h2><p>The requested service is not running.  Please try again later.</p></body></html>";

                                output.write((requestParts[2] + " 503 Service Unavailable\r\n").getBytes());
                                output.write(("Date: " + format.format(new Date()) + "\r\n").getBytes());
                                output.write("Content-Type: text/html\r\n".getBytes());
                                output.write(("Content-Length: " + responseContent.getBytes().length + "\r\n").getBytes());
                                output.write(("Connection: close\r\n\r\n").getBytes());
                                output.write(responseContent.getBytes());
                            }
                        }
                        else {
                            File requestFile = new File(Httpserver.rootDir.getCanonicalPath() + requestPath);
                            if (requestFile.exists()) {
                                if (requestFile.getCanonicalPath().startsWith(Httpserver.rootDir.getCanonicalPath()) && !requestFile.isHidden()) {
                                    Date lastModified = new Date(requestFile.lastModified());

                                    if (modifiedSince != null && lastModified.before(modifiedSince) && requestParts[0].equals("GET")) {
                                        output.write("HTTP/1.1 304 Not Modified\r\n".getBytes());
                                        output.write(("Date: " + format.format(new Date()) + "\r\n\r\n").getBytes());
                                    }
                                    else if (unmodifiedSince != null && lastModified.after(unmodifiedSince)) {
                                        output.write("HTTP/1.1 412 Precondition Failed\r\n\r\n".getBytes());
                                    }
                                    else {
                                        // Get mime type
                                        String mimeType;
                                        byte[] fileBytes;
                                        if (requestFile.isDirectory()) {
                                            if (!requestPath.endsWith("/")) {
                                                requestPath = requestPath + "/";
                                            }
                                            if (!requestParts[1].endsWith("/")) {
                                                requestParts[1] = requestParts[1] + "/";
                                            }
                                            mimeType = "text/html";
                                            StringBuffer dirListing = new StringBuffer();
                                            dirListing.append("<html><head><title>Index of " + requestPath + "</title></head>");
                                            dirListing.append("<body><h1>Index of " + requestPath + "</h1>");
                                            File[] files = requestFile.listFiles();
                                            for(int i = 0; i < files.length; ++i) {
                                                dirListing.append("<p><a href='" + requestParts[1] + URLEncoder.encode(files[i].getName(), "UTF-8") + "'>" + files[i].getName() + "</a></p>");
                                            }
                                            dirListing.append("</body></html>");
                                            fileBytes = dirListing.toString().getBytes();
                                            output.write((requestParts[2] + " 200 OK\r\n").getBytes());
                                            output.write(("Date: " + format.format(new Date()) + "\r\n").getBytes());
                                            output.write(("Content-Type: " + mimeType + "\r\n").getBytes());
                                            output.write(("Content-Length: " + fileBytes.length + "\r\n").getBytes());
                                            output.write(("Connection: close\r\n\r\n").getBytes());
                                            if (requestParts[0].equals("GET")) {
                                                output.write(fileBytes);
                                            }
                                        }
                                        else {
                                            if (requestFile.getName().endsWith(".css")) {
                                                // For some reason, built in mime type map doesn't
                                                // include mappings for css files
                                                mimeType = "text/css";
                                            }
                                            else {
                                                mimeType = new MimetypesFileTypeMap().getContentType(requestFile);
                                            }

                                            output.write((requestParts[2] + " 200 OK\r\n").getBytes());
                                            output.write(("Date: " + format.format(new Date()) + "\r\n").getBytes());
                                            output.write(("Content-Type: " + mimeType + "\r\n").getBytes());
                                            output.write(("Content-Length: " + requestFile.length() + "\r\n").getBytes());
                                            output.write(("Connection: close\r\n\r\n").getBytes());
                                            if (requestParts[0].equals("GET")) {
                                                FileInputStream fis = new FileInputStream(requestFile);
                                                int readValue;
                                                while ((readValue = fis.read()) != -1) {
                                                    output.write(readValue);
                                                }
                                            }
                                        }
                                    }
                                }
                                else {
                                    responseContent = "<html><body><h2>403 Forbidden</h2><p>Authorization required.</p></body></html>";

                                    output.write((requestParts[2] + " 403 Forbidden\r\n").getBytes());
                                    output.write(("Date: " + format.format(new Date()) + "\r\n").getBytes());
                                    output.write("Content-Type: text/html\r\n".getBytes());
                                    output.write(("Content-Length: " + responseContent.getBytes().length + "\r\n").getBytes());
                                    output.write(("Connection: close\r\n\r\n").getBytes());
                                    output.write(responseContent.getBytes());
                                }
                            }
                            else {
                                responseContent = "<html><body><h2>404 Not Found</h2><p>The requested path was not found.</p></body></html>";

                                output.write((requestParts[2] + " 404 Not Found\r\n").getBytes());
                                output.write(("Date: " + format.format(new Date()) + "\r\n").getBytes());
                                output.write("Content-Type: text/html\r\n".getBytes());
                                output.write(("Content-Length: " + responseContent.getBytes().length + "\r\n").getBytes());
                                output.write(("Connection: close\r\n\r\n").getBytes());
                                output.write(responseContent.getBytes());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            responseContent = "<html><body><h2>500 Internal Server Error</h2><p>An unexpected error occurred on the server.</p></body></html>";
            Httpserver.logError(ex, null);
            try {
                output.write(("HTTP/1.1 500 Internal Server Error\r\n").getBytes());
                output.write(("Date: " + format.format(new Date()) + "\r\n").getBytes());
                output.write("Content-Type: text/html\r\n".getBytes());
                output.write(("Content-Length: " + responseContent.getBytes().length + "\r\n").getBytes());
                output.write(("Connection: close\r\n\r\n").getBytes());
                output.write(responseContent.getBytes());
            }
            catch (IOException e) {
                Httpserver.logError(e, "Thread '" + this.getName() + "' could not write to the socket output stream.");
            }
        } finally {
            try {
                output.flush();
                output.close();
                input.close();
            }
            catch (IOException ex) {
                Httpserver.logError(ex, "Thread '" + this.getName() + "' had an error cleaning up the socket streams.");
            }
        }
    }
}
