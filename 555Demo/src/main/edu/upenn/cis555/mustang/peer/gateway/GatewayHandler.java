package edu.upenn.cis555.mustang.peer.gateway;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import edu.upenn.cis555.mustang.common.Queue;
import edu.upenn.cis555.mustang.peer.IndexApp;

public class GatewayHandler implements Runnable {
	private Queue<Socket> queue;
	private IndexApp indexApp;
	
	public GatewayHandler(IndexApp indexApp, Queue<Socket> queue) {
	    this.indexApp = indexApp;
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
		        RestHandler handler = new RestHandler(indexApp, input, output);  
                String line;
                int contentLength = RestHandler.UNKNOWN_CONTENT_LENGTH;
                while ((line = input.readLine()) != null) {
                    int length = handler.handleRequest(line);
                    if (length != RestHandler.UNKNOWN_CONTENT_LENGTH) {
                        contentLength = length; 
                    }
                    if (line.length() == 0) {
                        char[] content;
                        if (contentLength != RestHandler.UNKNOWN_CONTENT_LENGTH) {
                            content = new char[contentLength];
                            input.read(content);
                        }
                        else {
                            content = new char[0];
                        }
                        handler.handleResponse(String.valueOf(content));
                        break;
                    }
                }
		        socket.close();
    		} catch (IOException ioe) {
    		    // Do something here
    		}
		}
	}
}
