package edu.upenn.cis555.webserver;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ServletConfigImpl implements ServletConfig {
    private String name;
    private ServletContextImpl context;
    private Map<String,String> initParams;

    public ServletConfigImpl(String name, ServletContextImpl context) {
        this.name = name;
        this.context = context;
        initParams = new HashMap<String,String>();
    }
    
    @Override
    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    @Override
    public Enumeration getInitParameterNames() {
        Set<String> keys = initParams.keySet();
        Vector<String> atts = new Vector<String>(keys);
        return atts.elements();
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public String getServletName() {
        return name;
    }

    void setInitParam(String name, String value) {
        initParams.put(name, value);
    }
}
