package edu.upenn.cis555.mustang.webserver.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigHandler extends DefaultHandler {
	private int state;
	private String servletName;
	private String paramName;
	
	private String contextName;
	private Map<String, String> servlets;
	private Map<String, String> contextParams;
	private Map<String, Map<String, String>> servletParams;
	private Map<String, List<String>> urlPatterns;
	
	public ConfigHandler() {
		servlets = new HashMap<String, String>();
		contextParams = new HashMap<String, String>();
		servletParams = new HashMap<String, Map<String, String>>();
		urlPatterns = new HashMap<String, List<String>>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("servlet-name")) {
			state = 1;
		} else if (qName.equals("servlet-class")) {
			state = 2;
		} else if (qName.equals("context-param")) {
			state = 3;
		} else if (qName.equals("init-param")) {
			state = 4;
		} else if (qName.equals("param-name")) {
			state = state == 3 ? 10 : 20;	// else 4
		} else if (qName.equals("param-value")) {
			state = state == 10 ? 11 : 21;	// else 20
		} else if (qName.equals("url-pattern")) {
			state = 5;
		} else if (qName.equals("display-name")) {
			state = 6;
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) {
		String value = new String(ch, start, length);
		if (state == 1) {
			servletName = value;
			state = 0;
		} else if (state == 2) {
			servlets.put(servletName, value);
			state = 0;
		} else if (state == 10 || state == 20) {
			paramName = value;
		} else if (state == 11) {
			if (paramName == null) {
				System.err.println("Context parameter value '" + value + "' without name");
			}
			contextParams.put(paramName, value);
			paramName = null;
			state = 0;
		} else if (state == 21) {
			if (paramName == null) {
				System.err.println("Servlet parameter value '" + value + "' without name");
			}
			Map<String, String> params = servletParams.get(servletName);
			if (params == null) {
				params = new HashMap<String, String>();
				servletParams.put(servletName, params);
			}
			params.put(paramName, value);
			paramName = null;
			state = 0;
		} else if (state == 5) {
            List<String> patterns = urlPatterns.get(servletName);
            if (patterns == null) {
                patterns = new ArrayList<String>();
                urlPatterns.put(servletName, patterns);
            }
            patterns.add(value);	
            state = 0;
		} else if (state == 6) {
			contextName = value;
			state = 0;
		}
	}
	
	String getContextName() {
		return contextName;
	}
	
	Map<String, String> getServlets() {
		return servlets;
	}
	
	Map<String, Map<String, String>> getServletParameters() {
		return servletParams;
	}
	
	Map<String, String> getContextParameters() {
		return contextParams;
	}
	
	Map<String, String> getServletUrlMappings() {
		Map<String, String> mappings = new HashMap<String, String>();
		Comparator<String> comparator = new Comparator<String>() {
           public int compare(String string1, String string2) {
               return -string1.compareTo(string2);
           }
        };
		for (Map.Entry<String, List<String>> entry : urlPatterns.entrySet()) {
			List<String> patterns = entry.getValue();
			Collections.sort(patterns, comparator);
			for (String pattern : patterns) {
				mappings.put(pattern, entry.getKey());
			}
		}
		return mappings;
	}
}
