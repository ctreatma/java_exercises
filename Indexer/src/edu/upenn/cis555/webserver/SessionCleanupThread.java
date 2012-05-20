package edu.upenn.cis555.webserver;

public class SessionCleanupThread extends Thread {

    public SessionCleanupThread() {        
    }
    
    public void run() {
        while (true) {
            try {
                Thread.sleep(Httpserver.maxInactive * 1000);
                Httpserver.context.removeInvalidSessions();
            }
            catch (Exception ex) {
                Httpserver.logError(ex, null);
                System.exit(1);
            }
        }
    }
}
