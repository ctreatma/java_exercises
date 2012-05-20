package edu.upenn.cis555.webserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ServletContextImpl implements ServletContext {
    private Map<String,Object> attributes = new HashMap<String,Object>();
    private Map<String,HttpSessionImpl> sessions = new HashMap<String,HttpSessionImpl>();
    private Map<String,String> initParams = new HashMap<String,String>();
    private String name;
    private File contextPath;
    
    public ServletContextImpl(String name, File contextPath) {
        this.name = name;
        this.contextPath = contextPath;
    }
    
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Enumeration getAttributeNames() {
        Set<String> keys = attributes.keySet();
        Vector<String> atts = new Vector<String>(keys);
        return atts.elements();
    }

    public ServletContext getContext(String uripath) {
        return this;
    }

    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    public Enumeration getInitParameterNames() {
        Set<String> keys = initParams.keySet();
        Vector<String> atts = new Vector<String>(keys);
        return atts.elements();
    }

    public int getMajorVersion() {
        return 0;
    }

    public String getMimeType(String file) {
        // Not implemented per assignment directions
        return null;
    }

    public int getMinorVersion() {
        return 0;
    }

    public RequestDispatcher getNamedDispatcher(String arg0) {
        // Not implemented per assignment directions
        return null;
    }

    public String getRealPath(String path) {
        if (contextPath != null && contextPath.exists()) {
            try {
                File realPath = new File(contextPath.getCanonicalPath() + path);
                if (realPath.getCanonicalPath().startsWith(contextPath.getCanonicalPath())) {
                    return realPath.getCanonicalPath();
                }
                else {
                    // Don't return the real path for files outside of the context
                    return null;
                }
            }
            catch (IOException ex) {
                System.err.println("Error getting real path: " + path);
                ex.printStackTrace();
                return null;
            }
        }
        else {
            return null;
        }
    }

    public RequestDispatcher getRequestDispatcher(String arg0) {
        // Not implemented per assignment directions
        return null;
    }

    public URL getResource(String arg0) throws MalformedURLException {
        // Not implemented per assignment directions
        return null;
    }

    public InputStream getResourceAsStream(String arg0) {
        // Not implemented per assignment directions
        return null;
    }

    public Set getResourcePaths(String arg0) {
        // Not implemented per assignment directions
        return null;
    }

    public String getServerInfo() {
        return "CIS 555 Httpserver/" + getMajorVersion() + "." + getMinorVersion() + " (author: ctreatma)";
    }

    public Servlet getServlet(String name) throws ServletException {
        return null;
    }

    public String getServletContextName() {
        return name;
    }

    @Override
    public Enumeration getServletNames() {
        return Collections.enumeration(Collections.EMPTY_SET);
    }

    @Override
    public Enumeration getServlets() {
        return Collections.enumeration(Collections.EMPTY_SET);
    }

    @Override
    public void log(Exception exception, String msg) {
        log(msg, (Throwable) exception);
    }

    @Override   
    public void log(String msg) {
        System.err.println(msg);
    }

    @Override   
    public void log(String message, Throwable throwable) {
        System.err.println(message);
        throwable.printStackTrace(System.err);
    }

    @Override    
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override    
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }
 
    void setInitParam(String name, String value) {
        initParams.put(name, value);
    }
    
    void addSession(HttpSessionImpl session) {
        synchronized(sessions) {
            sessions.put(session.getId(), session);
        }
    }
    
    HttpSessionImpl getSession(String id) {
        synchronized(sessions) {
            HttpSessionImpl session = sessions.get(id);
            if (session != null && session.isValid()) {
                Date now = new Date();
                long lastAccess = session.getLastAccessedTime();
                long maxInactive = (long)session.getMaxInactiveInterval() * 1000L;
                if (now.getTime() - lastAccess > maxInactive) {
                    session.invalidate();
                    session = null;
                }
                else {
                    session.setLastAccessedTime(now);
                }
            }
            
            return session;
        }
    }

    void removeSession(String id) {
        synchronized(sessions) {
            sessions.remove(id);
        }
    }
    
    void removeInvalidSessions() {
        synchronized(sessions) {
            String[] keys = sessions.keySet().toArray(new String[sessions.keySet().size()]);
            for (String id : keys) {
                HttpSessionImpl session = sessions.get(id);
                if (session != null && session.isValid()) {
                    Date now = new Date();
                    long lastAccess = session.getLastAccessedTime();
                    long maxInactive = (long)session.getMaxInactiveInterval() * 1000L;
                    if (now.getTime() - lastAccess > maxInactive) {
                        session.invalidate();
                    }
                }
            }
        }
    }
}
