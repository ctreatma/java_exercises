package edu.upenn.cis555.mustang.webserver.support;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ServletContextSupport implements ServletContext {
	private Map<String, Object> attributes;
	private Map<String, String> initParams;
	private List<Servlet> servlets;
	private String name;
	
	public ServletContextSupport(String name) {
		attributes = new HashMap<String,Object>();
		initParams = new HashMap<String,String>();
		servlets = new ArrayList<Servlet>();
		this.name = name;
	}
	
	public Object getAttribute(String name) {
		return attributes.get(name);	}

	public Enumeration<String> getAttributeNames() {
		Set<String> keys = attributes.keySet();
		Vector<String> attribs = new Vector<String>(keys);
		return attribs.elements();
	}

	public ServletContext getContext(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInitParameter(String name) {
		return initParams.get(name);
	}

	public Enumeration<String> getInitParameterNames() {
		Set<String> keys = initParams.keySet();
		Vector<String> attribs = new Vector<String>(keys);
		return attribs.elements();
	}

	public int getMajorVersion() {
		return 2;
	}

	public String getMimeType(String file) {
		return null;
	}

	public int getMinorVersion() {
		return 4;
	}

	public RequestDispatcher getNamedDispatcher(String name) {
		throw new UnsupportedOperationException("not implemented");
	}

	public String getRealPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public RequestDispatcher getRequestDispatcher(String name) {
		throw new UnsupportedOperationException("not implemented");
	}

	public URL getResource(String arg0) throws MalformedURLException {
		throw new UnsupportedOperationException("not implemented");
	}

	public InputStream getResourceAsStream(String path) {
		throw new UnsupportedOperationException("not implemented");
	}

	public Set<String> getResourcePaths(String path) {
		throw new UnsupportedOperationException("not implemented");
	}

	public String getServerInfo() {
		return "HTTP Server";
	}

	@Deprecated
	public Servlet getServlet(String name) throws ServletException {
		for (Servlet servlet: servlets) {
			if (servlet.getServletConfig().getServletName().equals(name)) {
				return servlet;
			}
		}
		return null;
	}

	public String getServletContextName() {
		return name;
	}

	@Deprecated
	public Enumeration<String> getServletNames() {
		Vector<String> names = new Vector<String>();
		for (Servlet servlet: servlets) {
			names.add(servlet.getServletConfig().getServletName());
		}
		return names.elements();
	}

	@Deprecated
	public Enumeration<Servlet> getServlets() {
		return new Vector<Servlet>(servlets).elements();
	}

	public void log(String message) {
		throw new UnsupportedOperationException("not implemented");
	}

	public void log(Exception exception, String message) {
		throw new UnsupportedOperationException("not implemented");
	}

	public void log(String message, Throwable throwable) {
		throw new UnsupportedOperationException("not implemented");
	}

	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}
	
	void setInitParameter(String name, String value) {
		initParams.put(name, value);
	}
	
	void addServlet(Servlet servlet) {
		servlets.add(servlet);
	}
}
