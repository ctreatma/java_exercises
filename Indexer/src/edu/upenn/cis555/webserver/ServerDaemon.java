package edu.upenn.cis555.webserver;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.Socket;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerDaemon extends Thread {
    private ServerSocketChannel serverChannel;
    private RequestQueue requests;

    public ServerDaemon(int port, RequestQueue requests) throws IOException {
        this.requests = requests;
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
    }

    public void run() {
        while (true) {
            try {
                SocketChannel channel = serverChannel.accept();
                if (channel != null) {
                    Socket socket = channel.socket();
                    socket.setSoTimeout(30000);
                    requests.addRequest(socket);
                }
            }
            catch (Exception ex) {
                System.exit(1);
                Httpserver.logError(ex, "The server daemon encountered an error trying to open a socket.");
            }
        }
    }

    synchronized public void stopServer() {
        try {
            serverChannel.close();
        }
        catch (IOException ex) {
            Httpserver.logError(ex, "There was an error closing the server channel.");
        }
    }
    
    String getHostName() {
        return serverChannel.socket().getInetAddress().getHostName();
    }

    String getHostAddr() {
        return serverChannel.socket().getInetAddress().getHostAddress();
    }
}
