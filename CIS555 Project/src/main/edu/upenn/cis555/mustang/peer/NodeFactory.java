package edu.upenn.cis555.mustang.peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.IdFactory;
import rice.p2p.commonapi.Node;
import rice.pastry.NodeHandle;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.IPNodeIdFactory;

/**
 * Creates multiple Pastry nodes in a ring
 */
public class NodeFactory {
	private NodeIdFactory nidFactory;
	private SocketPastryNodeFactory factory;
	private IdFactory idFactory;
	private NodeHandle bootHandle;
	int createdCount = 0;
	int port;
	
	public NodeFactory(int port) {
		this(new Environment(), port);
	}	

	public NodeFactory(int port, InetSocketAddress bootstrap) {
		this(port);
		if (bootstrap != null) { 
			bootHandle = factory.getNodeHandle(bootstrap);
		}
	}
	
	public NodeFactory(Environment env, int port) {
		this.port = port;
		env.getParameters().setInt("pastry_socket_writer_max_queue_length", 3000);
//		env.getParameters().setBoolean("pastry_socket_allow_loopback", true);
//		nidFactory = new RandomNodeIdFactory(env);
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			nidFactory = new IPNodeIdFactory(localhost, port, env);
			factory = new SocketPastryNodeFactory(nidFactory, port, env);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage(), ioe);
		}
		idFactory = new PastryIdFactory(env);
	}
	
	public Node getNode() {
		try {
			synchronized (this) {
				if (bootHandle == null && createdCount > 0) {
					InetAddress localhost = InetAddress.getLocalHost();
					InetSocketAddress bootaddress = new InetSocketAddress(localhost, port);
					bootHandle = factory.getNodeHandle(bootaddress);
				}
			}
		} catch (UnknownHostException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		
		PastryNode node = factory.newNode(bootHandle);
		while (!node.isReady() && !node.joinFailed()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
			// abort if unable to join
			if (node.joinFailed()) {
				String op = bootHandle == null ? "start" : "join";		
				throw new RuntimeException("Failed to " + op + " the ring - " + node.joinFailedReason()); 
			}
		}
		System.out.println(bootHandle == null ? "starting " + node : "joining " + bootHandle + " as " + node);
		synchronized (this) {
			++createdCount;
		} 
		return node;
	}
	
	public void shutdownNode(Node n) {
		((PastryNode) n).destroy();
	}
/*	
	public Id getId(byte[] material) {
		return idFactory.buildId(material);
	}
*/	
	public Id getKey(String key) {
		return idFactory.buildId(key);
	}
	
	public Id getId(String id) { 
		return idFactory.buildIdFromToString(id);
	}
}

