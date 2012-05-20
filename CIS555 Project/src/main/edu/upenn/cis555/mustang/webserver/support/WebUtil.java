package edu.upenn.cis555.mustang.webserver.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import edu.upenn.cis555.mustang.webserver.ServerLog;

public class WebUtil {
	private static ServletConfigSupport config;
	private static ServletContextSupport context;
	private static Map<String, HttpServlet> servlets;
	// support servlet invocation by URL only
	private static Map<String, String> mappings;
	
	private WebUtil() {
	}
	
	public static void parseWebDotXml(String webDotXml) throws Exception {
		File file = new File(webDotXml);
		if (!file.exists()) {
			ServerLog.getLog().logMessage("Cannot find " + file);
			throw new FileNotFoundException("cannot find " + file);
		}
		
		ConfigHandler handler = new ConfigHandler();
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(file, handler);
		createConfig(handler);
		createServlets(handler);
		createMappings(handler);
	}	
	
	public static ServletConfigSupport getServletConfig() {
		return config;
	}

	public static Collection<HttpServlet> getHttpServlets() {
		return servlets.values();
	}
	
	public static HttpServlet getHttpServlet(String path) {
		// 1. exact match of the path
		for (Map.Entry<String, String> entry : mappings.entrySet()) {
			if ((path + "/").startsWith(entry.getKey() + "/")) {
				return servlets.get(entry.getValue());
			}
		}
		// 2. path mapping
		for (Map.Entry<String, String> entry : mappings.entrySet()) {
			if (path.startsWith(entry.getKey().replace("*", ""))) {
				return servlets.get(entry.getValue());
			}
		}
		return null;
	}	
	
	private static ServletConfigSupport createConfig(ConfigHandler handler) {
		if (config == null) {
			config = new ServletConfigSupport(createContext(handler));
		}
		return config;
	}
	
	private static ServletContextSupport createContext(ConfigHandler handler) {
		if (context == null) {
			context = new ServletContextSupport(handler.getContextName());
			for (Map.Entry<String, String> param : handler.getContextParameters().entrySet()) {
				context.setInitParameter(param.getKey(), param.getValue());
			}
		}
		return context;
	}
    
	@SuppressWarnings("unchecked")
	private static void createServlets(ConfigHandler handler) 
		throws Exception {
		if (servlets == null) {
			ServletContext servletContext = createContext(handler);
			servlets = new HashMap<String, HttpServlet>();
			for (String servletName : handler.getServlets().keySet()) {
				ServletConfigSupport config = new ServletConfigSupport(servletName, servletContext);
				String className = handler.getServlets().get(servletName);
				Class servletClass = Class.forName(className);
				HttpServlet servlet = (HttpServlet) servletClass.newInstance();
				Map<String, String> servletParams = handler.getServletParameters().get(servletName);
				if (servletParams != null) {
					for (Map.Entry<String, String> param : servletParams.entrySet()) {
						config.setInitParameter(param.getKey(), param.getValue());
					}
				}
				servlet.init(config);
				servlets.put(servletName, servlet);
				context.addServlet(servlet);
			}
		}
	}
	
	private static void createMappings(ConfigHandler handler) {
		if (mappings == null) {
			mappings = handler.getServletUrlMappings();
		}
	}
}
