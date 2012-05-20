package edu.upenn.cis555.mustang.webserver.support;

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

@SuppressWarnings("deprecation")
public class HttpSessionSupport implements HttpSession {
	static final String JSESSIONID = "JSESSIONID";
	
	private Properties props = new Properties();
	private boolean invalid;
	private String id;
	private long time;
	private int maxInactiveInterval;
	
	public HttpSessionSupport() {
		// not robust, but suffice to serve the purpose here
		Random random = new Random();
		byte[] bytes = new byte[16];
		random.nextBytes(bytes);
		id = String.valueOf(bytes);
		time = new Date().getTime();
	}
	
	public Object getAttribute(String name) {
		return props.get(name);
	}

	public Enumeration<String> getAttributeNames() {
		Vector<String> attribs = new Vector<String>();
		Enumeration<Object> keys = props.keys();
		while (keys.hasMoreElements()) {
			attribs.add(keys.nextElement().toString());
		}
		return attribs.elements();
	}

	public long getCreationTime() {
		return time;
	}

	public String getId() {
		return id;
	}

	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpSessionContext getSessionContext() {
		return null;
	}

	public Object getValue(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public void invalidate() {
		props.clear();
		invalid = true;
	}

	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	public void putValue(String arg0, Object arg1) {
		// TODO Auto-generated method stub
	}

	public void removeAttribute(String name) {
		props.remove(name);
	}

	public void removeValue(String arg0) {
		// TODO Auto-generated method stub
	}

	public void setAttribute(String name, Object value) {
		props.put(name, value);
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}
	
	boolean isValid() {
		return !invalid;
	}
}
