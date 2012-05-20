package edu.upenn.cis555.youtube;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.upenn.cis555.webserver.RequestQueue;
import edu.upenn.cis555.webserver.ThreadPool;

public class PastryThread extends Thread {
    protected static final String REST_PATH = "/youTubeSearch/";
    private static final String REST_PATTERN = REST_PATH + "([^\\/]*)/?";
    private ThreadPool owner;
    private RequestQueue requests;
    private PastryMap messages;
    private P2PCache cacheApplication;
    
    public PastryThread(ThreadPool owner, RequestQueue requests, PastryMap messages, P2PCache cacheApplication) {
        this.owner = owner;
        this.requests = requests;
        this.messages = messages;
        this.cacheApplication = cacheApplication;
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
            ex.printStackTrace();
        }
        finally {
            try {
                socket.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            owner.finishRequest();
        }
    }

    private void handleRequest(InputStream input, OutputStream output, Socket socket) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        
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
                if (!(requestParts[0].equals("GET"))) {
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
                            if (headerName.equalsIgnoreCase("Host")) {
                                includesHost = true;
                            }
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
                        String requestPath = URLDecoder.decode(requestParts[1], "UTF-8");
                        
                        Pattern pattern = Pattern.compile("https?://[^/]*(/.*)");
                        Matcher matcher = pattern.matcher(requestPath);
                        if (matcher.matches()) {
                            requestPath = matcher.group(1);
                        }
                        
                        pattern = Pattern.compile(PastryThread.REST_PATTERN);
                        matcher = pattern.matcher(requestPath);
                        if (matcher.matches()) {
                            String keyword = matcher.group(1);                                
                            String mapKey = UUID.randomUUID().toString();
                            
                            cacheApplication.route(mapKey, keyword);
                                
                            YouTubeMessage message = messages.getMessage(mapKey, YouTubeMessage.Type.RESULT);
                                
                            responseContent = message.getVideoFeedHtml();
                                
                            output.write((requestParts[2] + " 200 OK\r\n").getBytes());
                            output.write(("Date: " + format.format(new Date()) + "\r\n").getBytes());
                            output.write("Content-Type: text/html\r\n".getBytes());
                            output.write(("Content-Length: " + responseContent.getBytes().length + "\r\n").getBytes());
                            output.write(("Connection: close\r\n\r\n").getBytes());
                            output.write(responseContent.getBytes());
                        }
                        else {
                            responseContent = "<html><body><h2>404 Not Found</h2><p>Could not find the requested resource</p></body></html>";

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
        } catch (Exception ex) {
            responseContent = "<html><body><h2>500 Internal Server Error</h2><p>An unexpected error occurred on the server.</p></body></html>";
            ex.printStackTrace();
            try {
                output.write(("HTTP/1.1 500 Internal Server Error\r\n").getBytes());
                output.write(("Date: " + format.format(new Date()) + "\r\n").getBytes());
                output.write("Content-Type: text/html\r\n".getBytes());
                output.write(("Content-Length: " + responseContent.getBytes().length + "\r\n").getBytes());
                output.write(("Connection: close\r\n\r\n").getBytes());
                output.write(responseContent.getBytes());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                output.flush();
                output.close();
                input.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
