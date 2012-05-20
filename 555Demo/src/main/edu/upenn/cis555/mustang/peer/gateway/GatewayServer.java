package edu.upenn.cis555.mustang.peer.gateway;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.upenn.cis555.mustang.common.Queue;
import edu.upenn.cis555.mustang.peer.IndexApp;
import edu.upenn.cis555.mustang.webserver.QueueListener;

public class GatewayServer { 
    private int port;
    
    private QueueListener daemon;
    private ThreadPool threadPool;
    private IndexApp indexApp;
    
    public GatewayServer(int port, IndexApp indexApp) {
        this.port = port;
        this.indexApp = indexApp;
    }
    
    public void startup() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Queue<Socket> queue = new Queue<Socket>();
        daemon = new QueueListener(serverSocket, queue);
        new Thread(daemon, "daemon").start();
        threadPool = new ThreadPool(ThreadPool.DEFAULT_SIZE, indexApp, queue);
        threadPool.start();
    }
    
    public void shutdown() {
        daemon.stop();
        threadPool.stop();
        System.out.println("Server shut down");
    }
}
