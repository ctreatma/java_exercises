package edu.upenn.cis555.webserver;

import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class HttpSessionImpl implements HttpSession {
    private Map<String, Object> attributes = new HashMap<String,Object>();
    private UUID id = UUID.randomUUID();
    private Date creationTime = new Date();
    private Date lastAccessedTime = new Date();
    private int maxInactive = Httpserver.maxInactive;
    private boolean isValid = true;
    private boolean isNew = true;
    private ServletContextImpl context = Httpserver.context;
    
    public HttpSessionImpl() {
        if (context != null) {
            context.addSession(this);
        }
    }
    
    @Override
    public Object getAttribute(String name) {
        if (isValid) {
            return attributes.get(name);
        }
        else {
            throw new IllegalStateException("The session " + id + " is invalid.");
        }
    }

    @Override
    public Enumeration getAttributeNames() {
        if (isValid) {
            return Collections.enumeration(attributes.keySet());
        }
        else {
            throw new IllegalStateException("The session " + id + " is invalid.");
        }
    }

    @Override
    public long getCreationTime() {
        if (isValid) {
            return creationTime.getTime();
        }
        else {
            throw new IllegalStateException("The session " + id + " is invalid.");
        }
    }

    @Override
    public String getId() {
        if (isValid) {
            return id.toString();
        }
        else {
            throw new IllegalStateException("The session " + id + " is invalid.");
        }
    }

    @Override
    public long getLastAccessedTime() {
        if (isValid) {
            return lastAccessedTime.getTime();
        }
        else {
            throw new IllegalStateException("The session " + id + " is invalid.");
        }
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactive;
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        // HttpSessionContext is deprecated;  return null.
        return null;
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public String[] getValueNames() {
        if (isValid) {
            return attributes.keySet().toArray(new String[attributes.keySet().size()]);
        }
        else {
            throw new IllegalStateException("The session " + id + " is invalid.");
        }
    }

    @Override
    public void invalidate() {
        context.removeSession(id.toString());
        isValid = false;
    }

    @Override
    public boolean isNew() {
        if (isValid) {
            return isNew;
        }
        else {
            throw new IllegalStateException("The session " + id + " is invalid.");
        }
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        if (isValid) {
            attributes.remove(name);
        }
        else {
            throw new IllegalStateException("The session " + id + " is invalid.");
        }
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (isValid) {
            attributes.put(name, value);
        }
        else {
            throw new IllegalStateException("The session " + id + " is invalid.");
        }
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactive = interval;
    }

    boolean isValid() {
        return isValid;
    }

    void setLastAccessedTime(Date lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }
    
    void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
