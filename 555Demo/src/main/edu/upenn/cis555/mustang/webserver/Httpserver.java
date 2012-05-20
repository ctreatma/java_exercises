package edu.upenn.cis555.mustang.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.servlet.http.HttpServlet;

import edu.upenn.cis555.mustang.common.Queue;
import edu.upenn.cis555.mustang.webserver.support.WebUtil;

public class Httpserver {
	private static final int PORT = 8080;
	
	private int port;
	private String documentRoot;
	private String webDotXml;
	
	private QueueListener daemon;
	private ThreadPool threadPool;
	
	public Httpserver(int port, String documentRoot, String webDotXml) {
		this.port = port;
		this.documentRoot = documentRoot;
		this.webDotXml = webDotXml;
	}
	
	void startup() throws IOException {
		System.out.println("\nServer starting up...");
		try {
			ServerLog.getLog().logMessage("Parsing deployment descriptor file " + webDotXml);
			WebUtil.parseWebDotXml(webDotXml);
		} catch (Exception e) {
			ServerLog.getLog().logMessage("Unable to serve servlets - " + e.getMessage());
		}
		
		ServerSocket serverSocket = new ServerSocket(port);
		Queue<Socket> queue = new Queue<Socket>();
		daemon = new QueueListener(serverSocket, queue);
		new Thread(daemon, "daemon").start();
		threadPool = new ThreadPool(ThreadPool.DEFAULT_SIZE, documentRoot, queue);
		threadPool.start();
	}
	
	void shutdown() {
		System.out.println("Server shutting down...");
		ServerLog.getLog().logMessage("Server shut down");
    	ServerLog.getLog().closeLog();
    	daemon.stop();
    	threadPool.stop();
    	// destroy all the servlets
    	Collection<HttpServlet> servlets = WebUtil.getHttpServlets();
    	for (HttpServlet httpServlet : servlets) {
    		httpServlet.destroy();
    	}
    	System.out.println("Server shut down");
    }
    
    void pollThreadPool() {
    	threadPool.status();
    }
    
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: WebServer <port> <document root> <web.xml>");
			return;
		}
		
//		System.setProperty("line.separator", new String(new byte[] { 0xD, 0xA }));
		int port;
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException ignored) {
			port = PORT;
		}
		String documentRoot = args[1];
		String webDotXml = args[2];
		
		Httpserver webServer = new Httpserver(port, documentRoot, webDotXml);
		try {
			webServer.startup();
			System.out.println("Server started\n");
			ServerLog.getLog().logMessage("Server started");
		} catch (IOException ioe) {
			System.out.println("Server failed to start\n");
			return;
		}
		System.out.println("Interactive Menu:");
		System.out.println("1. shutdown");
		System.out.println("2. show thread status");
		System.out.println("3. view log");

		boolean quit = false;
		Scanner scanner = new Scanner(System.in);
		String choice = null;
		while (!quit) {
			System.out.print("Enter a number in the menu: ");
			try {
				choice = scanner.next();
				if ("1".equalsIgnoreCase(choice)) { 
					webServer.shutdown();
					quit = true;
				} else if ("2".equalsIgnoreCase(choice)) {
					webServer.pollThreadPool();
				} else if ("3".equalsIgnoreCase(choice)) {
					ServerLog.getLog().viewLog();
				} else {		
					System.out.println("Sorry but I dunno wot 2 do.");
				}
			} catch (NoSuchElementException e) { 
				quit = true;
			}
		}
	}
}
