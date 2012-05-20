package edu.upenn.cis555.mustang.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.upenn.cis555.mustang.common.Queue;

public class QueueListener implements Runnable {
	private volatile boolean quit;
    private ServerSocket serverSocket;
	private Queue<Socket> queue;
	
	public QueueListener(ServerSocket serverSocket, Queue<Socket> queue) {
        this.serverSocket = serverSocket;
        this.queue = queue;
	}
	
	public void run() {
        try {
        	Socket socket; 
            while (!quit) {
            	socket = serverSocket.accept();
                queue.enqueue(socket);
            }
        } catch (IOException e) { }
	}
	
    public void stop() {
        try {
        	quit = true;
            serverSocket.close();
            ServerLog.getLog().logMessage("Daemon shut down");
        } catch(IOException e) {
        	ServerLog.getLog().logMessage("Failed to shut down daemon");
        }
        serverSocket = null;
    }    
}
