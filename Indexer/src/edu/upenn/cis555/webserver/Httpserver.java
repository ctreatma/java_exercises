package edu.upenn.cis555.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class Httpserver {
    private static final int poolSize = 10;
    private static final String logFileName = "errorLog.txt";
    private static boolean quit = false;
    private static boolean hasServlet = false;
    private static File logFile;
    private static PrintStream logFileWriter;
    
    protected static SessionCleanupThread cleanup;
    protected static int port, maxInactive;
    protected static String serverName, serverAddr;
    protected static Handler h = null;
    protected static ServletContextImpl context = null;
    protected static HashMap<String,HttpServlet> servlets = null;
    protected static File rootDir;
    protected static RequestQueue requests;
    protected static ThreadPool requestHandlers = null;
    protected static ServerDaemon server = null;

    // Handler code taken from TestHandler
    static class Handler extends DefaultHandler {
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.compareTo("servlet-name") == 0) {
                m_state = 1;
            } else if (qName.compareTo("servlet-class") == 0) {
                m_state = 2;
            } else if (qName.compareTo("context-param") == 0) {
                m_state = 3;
            } else if (qName.compareTo("init-param") == 0) {
                m_state = 4;
            } else if (qName.compareTo("param-name") == 0) {
                m_state = (m_state == 3) ? 10 : 20;
            } else if (qName.compareTo("param-value") == 0) {
                m_state = (m_state == 10) ? 11 : 21;
            } else if (qName.compareTo("url-pattern") == 0) {
                m_state = 5;
            } else if (qName.compareTo("display-name") == 0) {
                m_state = 6;
            } else if (qName.compareTo("session-timeout") == 0) {
                m_state = 7;
            } else if (qName.compareTo("description") == 0) {
                m_state = 8;
            } else {
                m_state = 9;
            }
        }
        public void characters(char[] ch, int start, int length) {
            String value = new String(ch, start, length);
            if (m_state == 1) {
                m_servletName = value;
                m_state = 0;
            } else if (m_state == 2) {
                m_servlets.put(m_servletName, value);
                m_state = 0;
            } else if (m_state == 10 || m_state == 20) {
                m_paramName = value;
            } else if (m_state == 11) {
                if (m_paramName == null) {
                    Httpserver.logError("Context parameter value '" + value + "' without name");
                    System.exit(-1);
                }
                m_contextParams.put(m_paramName, value);
                m_paramName = null;
                m_state = 0;
            } else if (m_state == 21) {
                if (m_paramName == null) {
                    Httpserver.logError("Servlet parameter value '" + value + "' without name");
                    System.exit(-1);
                }
                HashMap<String,String> p = m_servletParams.get(m_servletName);
                if (p == null) {
                    p = new HashMap<String,String>();
                    m_servletParams.put(m_servletName, p);
                }
                p.put(m_paramName, value);
                m_paramName = null;
                m_state = 0;
            } else if (m_state == 5) {
                urlPatterns.put(value, m_servletName);
            } else if (m_state == 6) {
                displayName = value;
            } else if (m_state == 7) {
                maxInactive = Integer.parseInt(value) * 60;
            }
        }
        private int m_state = 0;
        private String m_servletName;
        private String m_paramName;
        String displayName;
        HashMap<String,String> urlPatterns = new HashMap<String,String>();
        HashMap<String,String> m_servlets = new HashMap<String,String>();
        HashMap<String,String> m_contextParams = new HashMap<String,String>();
        HashMap<String,HashMap<String,String>> m_servletParams = new HashMap<String,HashMap<String,String>>();
    }
        
    private static Handler parseWebdotxml(String webdotxml) throws Exception {
        Handler h = new Handler();
        File file = new File(webdotxml);
        if (file.exists() == false) {
            Httpserver.logError("error: cannot find '" + file.getPath() + "'");
            System.exit(-1);
        }
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(file, h);
        
        return h;
    }
    
    private static ServletContextImpl createContext(Handler h, File webappDir) {
        ServletContextImpl fc = new ServletContextImpl(h.displayName, webappDir);
        fc.setAttribute("ServletContext", h.displayName);
        for (String param : h.m_contextParams.keySet()) {
            fc.setInitParam(param, h.m_contextParams.get(param));
        }
        return fc;
    }
    
    private static HashMap<String,HttpServlet> createServlets(Handler h, ServletContextImpl fc) throws Exception {
        HashMap<String,HttpServlet> servlets = new HashMap<String,HttpServlet>();
        for (String servletName : h.m_servlets.keySet()) {
            ServletConfigImpl config = new ServletConfigImpl(servletName, fc);
            String className = h.m_servlets.get(servletName);
            Class servletClass = Class.forName(className);
            HttpServlet servlet = (HttpServlet) servletClass.newInstance();
            HashMap<String,String> servletParams = h.m_servletParams.get(servletName);
            if (servletParams != null) {
                for (String param : servletParams.keySet()) {
                    config.setInitParam(param, servletParams.get(param));
                }
            }
            servlet.init(config);
            servlets.put(servletName, servlet);
        }
        return servlets;
    }


    private static void usage() {
        System.err.println("Usage: java edu.upenn.cis555.webserver.Httpserver <port> <server root> [<path to web.xml>]");
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 2 || args.length > 3) {
            usage();
            System.exit(1);
        }
        else {
            try {
                port = Integer.valueOf(args[0]);
                String rootPath = args[1];
                rootDir = new File(rootPath);
                if (rootDir.isDirectory() && rootDir.isAbsolute()) {
                    
                    requests = new RequestQueue();

                    try {
                        server = new ServerDaemon(port, requests);
                    }
                    catch (IOException ex) {
                        Httpserver.logError("The server could not listen on port " + port);
                        System.exit(1);
                    }
                    
                    serverName = server.getHostName();
                    serverAddr = server.getHostAddr();
                    
                    server.start();

                    requestHandlers = new ThreadPool(poolSize, requests);

                    Runtime.getRuntime().addShutdownHook(new ShutdownThread());
                    
                    System.out.println();
                    System.out.println("-----------------------------------------------");
                    System.out.println("Welcome to Charles Treatman's CIS555 Web Server");
                    System.out.println("-----------------------------------------------");
                    System.out.println();

                    InputStreamReader converter = new InputStreamReader(System.in);
                    BufferedReader in = new BufferedReader(converter);

                    if (args.length == 3) {
                        h = parseWebdotxml(args[2]);
                        File webappDir = null;
                        try {
                            // I'm making the assumption here that the webapp follows the usual conventions
                            // for layout, so web.xml is located in {webappDir}/WEB-INF/web.xml
                            webappDir = new File(args[2]);
                            webappDir = new File(webappDir.getCanonicalPath()).getParentFile();
                            webappDir = webappDir.getParentFile();
                        }
                        catch (Exception ex) {
                            Httpserver.logError(ex, null);
                        }

                        context = createContext(h, webappDir);
                        
                        cleanup = new SessionCleanupThread();
                        cleanup.setDaemon(true);
                        cleanup.start();
                        
                        hasServlet = true;
                    }
                    
                    while (!quit) {
                        String status = (servlets != null) ? "running" : "stopped";
                        System.out.println("The web application is currently " + status);
                        System.out.println("Choose a menu option:");
                        if (hasServlet) {
                            System.out.println("- 'start' application");
                            System.out.println("- 'stop' application");
                        }
                        System.out.println("- 'error' log");
                        System.out.println("- 'status' of request threads");
                        System.out.println("- 'quit' Httpserver");

                        String option = "";
                        option = in.readLine();

                        if (hasServlet) {
                            if (option.equals("start")) {
                                if (servlets == null) { 
                                    servlets = createServlets(h, context);
                                }
                            }
                            else if (option.equals("stop")) {
                                if (servlets != null) {
                                    for(String servletName : servlets.keySet()) {
                                        servlets.get(servletName).destroy();
                                    }
                                    servlets = null;
                                }
                            }
                        }
                        if (option.equalsIgnoreCase("error")) {
                            Httpserver.showErrorLog();
                        }
                        else if (option.equalsIgnoreCase("status")) {
                            System.out.println(requestHandlers.getPoolStatus());
                        }
                        else if (option.equalsIgnoreCase("quit")) {
                            quit = true;
                        }

                        System.out.println();
                        System.out.println();
                    }
                }
                else {
                    Httpserver.logError("Bad root path supplied.");
                }
            }
            catch (Exception ex) {
                Httpserver.logError(ex, null);
                System.exit(1);
            }
        }
        System.exit(0);
    }
    
    static void logError(String msg) {
        try {
            checkLogFileExists();
            logFileWriter.println("<<<<<<<<<<<");
            logFileWriter.println(new Date());
            logFileWriter.println(msg);
            logFileWriter.println(">>>>>>>>>>>");
            logFileWriter.println();
            logFileWriter.flush();
        }
        catch (IOException e) {
            // Can't log this error, so dump stack trace to stderr?
            e.printStackTrace();
        }
        
    }
    
    static void logError(Exception ex, String msg) {
        try {
            checkLogFileExists();
            logFileWriter.println("<<<<<<<<<<<");
            logFileWriter.println(new Date());
            if (msg != null) {
                logFileWriter.println(msg);
            }
            ex.printStackTrace(logFileWriter);
            logFileWriter.println(">>>>>>>>>>>");
            logFileWriter.println();
            logFileWriter.flush();
        }
        catch (IOException e) {
            // Can't log this error, so dump stack trace to stderr?
            e.printStackTrace();
            System.err.println("Encountered while logging: ");
            ex.printStackTrace();
        }
        
    }
    
    private static void showErrorLog() {
        try {
            checkLogFileExists();
            BufferedReader logReader = new BufferedReader(new FileReader(logFile));

            String line;
            System.out.println();
            while ((line = logReader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println();
        }
        catch (Exception ex) {
            // Can't log this error, so dump stack trace to stderr?
            ex.printStackTrace();
        }
    }
    
    private static void checkLogFileExists() throws IOException {
        if (logFile == null) {
            logFile = new File(logFileName);
        }
        if (logFile.createNewFile() || logFileWriter == null) {
            logFileWriter = new PrintStream(logFile);            
        }
        
    }
}
