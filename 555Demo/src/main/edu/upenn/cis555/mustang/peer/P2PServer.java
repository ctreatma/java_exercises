package edu.upenn.cis555.mustang.peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import rice.pastry.PastryNode;

import edu.upenn.cis555.mustang.common.Queue;
import edu.upenn.cis555.mustang.crawl.Crawler;
import edu.upenn.cis555.mustang.crawl.Document;
import edu.upenn.cis555.mustang.datastore.DataRepository;
import edu.upenn.cis555.mustang.index.DistributedHashKey;
import edu.upenn.cis555.mustang.index.Indexer;
import edu.upenn.cis555.mustang.peer.IndexApp;
import edu.upenn.cis555.mustang.peer.gateway.GatewayServer;
import edu.upenn.cis555.mustang.webserver.QueueListener;

public class P2PServer {
	private static final int numPages = 50;

	private InetSocketAddress bootAddress;
	private int pastryPort, gatewayPort;
	private String dataStorePath;
	private IndexApp indexApp;
	private CrawlerApp crawlerApp;
	private GatewayServer gateway;
	private QueueListener daemon;
	private ThreadPool threadPool;
	private DistributedHashKey hashKey;

	public P2PServer(InetSocketAddress bootAddress, int pastryPort,
		int gatewayPort, String dataStorePath) {
		this.bootAddress = bootAddress;
		this.pastryPort = pastryPort;
		this.dataStorePath = dataStorePath;
		this.gatewayPort = gatewayPort;
	}

	void launch() throws IOException {
		NodeFactory nodeFactory = new NodeFactory(pastryPort, bootAddress);
		DataRepository dataRepo = new DataRepository(dataStorePath);
		indexApp = new IndexApp(nodeFactory, dataRepo);
		// ((PastryNode) indexApp.getNode()).boot(bootAddress);
		crawlerApp = new CrawlerApp(nodeFactory, indexApp.getNode());
		hashKey = new NodeHashKey(nodeFactory);
		// start crawler
		Queue<Document> queue = new Queue<Document>(); // shared by crawler
		Crawler crawler = new Crawler(numPages, queue, hashKey, crawlerApp);
		new Thread(crawler, "crawler").start();
		// start indexer
		Indexer indexer = new Indexer(queue, hashKey, indexApp, dataRepo);
		new Thread(indexer, "indexer").start();
	}

	private void startCrawler() {
		List<String> seedURLs = new ArrayList<String>();
		seedURLs.add("http://www.cnn.com");
		seedURLs.add("http://www.upenn.edu");
		seedURLs.add("http://www.nytimes.com");
		seedURLs.add("http://www.cnet.com");

		crawlerApp.sendLinks(seedURLs);
	}

	private void startPageRank() throws IOException {
		gateway = new GatewayServer(gatewayPort, indexApp);
		gateway.startup();
	}

	void startSearcher() throws IOException {
		// start gateway
		ServerSocket serverSocket = new ServerSocket(gatewayPort);
		Queue<Socket> gatewayQueue = new Queue<Socket>();
		daemon = new QueueListener(serverSocket, gatewayQueue);
		new Thread(daemon, "daemon").start();
		threadPool = new ThreadPool(ThreadPool.DEFAULT_SIZE, hashKey, indexApp, gatewayQueue);
		threadPool.start();
	}

	void shutdown() throws IOException {
	    if (gateway != null) 
	        gateway.shutdown();
	    if (daemon != null)
	        daemon.stop();
	    if (threadPool != null)
	        threadPool.stop();
	    ((PastryNode) indexApp.getNode()).destroy();
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 3 || args.length > 4) {
			System.out.println("Usage: P2PServer <bootstrap node IP address> <bootstrap node port> <data store directory> [<search engine port>]");
			return;
		}

		InetAddress bootHost = null;
		if (!"127.0.0.1".equals(args[0])
				&& !"localhost".equalsIgnoreCase(args[0])) {
			bootHost = InetAddress.getByName(args[0]);
		}
		int bootPort = Integer.parseInt(args[1]);
		InetSocketAddress bootAddress = null;
		if (bootHost != null) {
			bootAddress = new InetSocketAddress(bootHost, bootPort);
		}

		String dataStorePath = args[2];

		int port = 0;
		if (args.length > 3) {
			port = Integer.parseInt(args[3]);
		}

		P2PServer p2pServer = new P2PServer(bootAddress, bootPort, port,
				dataStorePath);
		p2pServer.launch();
		/*
		 * InetSocketAddress bootAddress = null; // new
		 * InetSocketAddress(InetAddress.getLocalHost(), 9001); new
		 * P2PServer(bootAddress, 9001, 8108, "target/datastore").launch();
		 */

		System.out.println("Interactive Menu:");
		System.out.println("1. crawl");
		System.out.println("2. PageRank");
		System.out.println("3. search");
		System.out.println("4. shutdown");

		boolean quit = false;
		Scanner scanner = new Scanner(System.in);
		String choice = null;
		while (!quit) {
			System.out.print("Enter a number in the menu: ");
			try {
				choice = scanner.next();
				if ("1".equalsIgnoreCase(choice)) {
					p2pServer.startCrawler();
				} else if ("2".equalsIgnoreCase(choice)) {
					p2pServer.startPageRank();
				} else if ("3".equalsIgnoreCase(choice)) {
					p2pServer.startSearcher();
				} else if ("4".equalsIgnoreCase(choice)) {
					p2pServer.shutdown();
					quit = true;
				} else {
					System.out.println("Sorry but I dunno wot 2 do.");
				}
			} catch (NoSuchElementException e) {
				quit = true;
			}
		}
	}

}
