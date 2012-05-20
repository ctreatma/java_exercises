package edu.upenn.cis555.mustang.webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import edu.upenn.cis555.mustang.common.Queue;
import edu.upenn.cis555.mustang.webserver.support.HttpHandler;

public class RequestHandler implements Runnable {
	private Queue<Socket> queue;
	private String documentRoot;
	
	public RequestHandler(String documentRoot, Queue<Socket> queue) {
		this.documentRoot = documentRoot;
		this.queue = queue;
	}
	
	public void run() {
    	while (true) {
    		Socket socket = queue.dequeue();
    		if (socket == null) {
    			break;
    		}
    		try {
		        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));            
		        DataOutputStream output = new DataOutputStream(socket.getOutputStream()); 
		        HttpHandler handler = new HttpHandler(socket.getLocalPort(), documentRoot, input, output); 
		        String line;
		        int contentLength = HttpHandler.UNKNOWN_CONTENT_LENGTH;
		        while (!socket.isClosed() && socket.isConnected() && (line = input.readLine()) != null) {
		        	int length = handler.handleRequest(line);
		        	if (length != HttpHandler.UNKNOWN_CONTENT_LENGTH) {
		        		contentLength = length; 
		        	}
		        	if (line.length() == 0 && contentLength != HttpHandler.UNKNOWN_CONTENT_LENGTH) {
				        char[] content = new char[contentLength];
			        	input.read(content);
			        	handler.handleRequest(String.valueOf(content));
/*		        		StringBuilder content = new StringBuilder();
		        		while (input.ready()) {
		        			content.append((char) input.read());
		        		}
		        		handler.handleRequest(content.toString()); */
	        			break;
		        	}
		        }
		        socket.close();
    		} catch (IOException ioe) {
    			ServerLog.getLog().logMessage(Thread.currentThread().getName() +  
    				" - " + "Failed to write to or close socket: " + ioe);
    		}
		}
	}
}
