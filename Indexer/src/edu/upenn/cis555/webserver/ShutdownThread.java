package edu.upenn.cis555.webserver;

public class ShutdownThread extends Thread {
    
    public ShutdownThread() {
    }
    
    public void run() {
        System.out.println("Shutting down.");
        Httpserver.server.stopServer();
        Httpserver.requestHandlers.shutDown();
        if (Httpserver.servlets != null) {
            for(String servletName : Httpserver.servlets.keySet()) {
                Httpserver.servlets.get(servletName).destroy();
            }
            Httpserver.servlets = null;
        }
        System.out.println("Done.");
    }
}
