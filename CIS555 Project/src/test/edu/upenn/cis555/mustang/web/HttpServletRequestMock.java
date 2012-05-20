package edu.upenn.cis555.mustang.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

public class HttpServletRequestMock extends HttpServletRequestStub {
	
	private Map<String, List<String>> params = new ConcurrentHashMap<String, List<String>>();
	private String method;

	public String getParameter(String name) {
		if (params.get(name) == null || params.get(name).isEmpty()) {
			return null;
		}
		return params.get(name).get(0);
	}
	
	void setParameter(String name, String value) {
		List<String> values = params.get(name);
		if (values == null) {
			values = new ArrayList<String>();
			params.put(name, values);
		}
		values.add(value);
	}
	
	public String getMethod() {
		return method;
	}
	
	void setMethod(String method) {
		this.method = method;
	}
	
	public HttpSession getSession() {
		return getSession(true);
	}

	public HttpSession getSession(boolean create) {
		return new HttpSessionMock();
	}
	
}
