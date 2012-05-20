package edu.upenn.cis555.mustang.webserver.support;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ServletConfigSupport implements ServletConfig {
	private String name;
	private ServletContext context;
	private Map<String, String> initParams;
	
	public ServletConfigSupport(ServletContext context) {
		this.context = context;
		initParams = new HashMap<String, String>();
	}
	
	public ServletConfigSupport(String name, ServletContext context) {
		this(context);
		this.name = name;
	}

	public String getInitParameter(String name) {
		return initParams.get(name);
	}
	
	public Enumeration<String> getInitParameterNames() {
		Set<String> keys = initParams.keySet();
		Vector<String> attribs = new Vector<String>(keys);
		return attribs.elements();
	}
	
	public ServletContext getServletContext() {
		return context;
	}
	
	public String getServletName() {
		return name;
	}

	public void setInitParameter(String name, String value) {
		initParams.put(name, value);
	}
}
