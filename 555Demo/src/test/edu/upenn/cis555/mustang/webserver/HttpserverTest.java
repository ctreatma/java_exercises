package edu.upenn.cis555.mustang.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.TestCase;
import edu.upenn.cis555.mustang.webserver.support.WebUtil;

public class HttpserverTest extends TestCase {
	private static final int PORT = 8080;
	private static final String DOCUMENT_ROOT = ".";
	private static final String WEB_DOT_XML = "target/WEB-INF/web.xml";
	private static final String URL = "http://localhost:" + PORT + "/search";

	private Httpserver webServer;
	
	public HttpserverTest(String testName) {
		super(testName);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		webServer = new Httpserver(PORT, DOCUMENT_ROOT, WEB_DOT_XML);
		webServer.startup();
	}

	public void testServerRunning() {
		assertEquals(WebUtil.getHttpServlets().size(), 1);
		assertEquals(WebUtil.getHttpServlet("/search").getServletName(), "webSearch");
		assertNotNull(WebUtil.getHttpServlet("/search").getInitParameter("searchEngine"));
		assertNotNull(WebUtil.getHttpServlet("/search").getInitParameter("searchEnginePort"));
		assertEquals(WebUtil.getServletConfig().getServletContext().getServletContextName(), "Test servlets");		
		assertEquals(WebUtil.getServletConfig().getServletContext().getInitParameter("webmaster"), "maz@seas.upenn.edu");
		assertEquals(WebUtil.getServletConfig().getServletContext().getInitParameter("BDBstore"), "target/datastore");
	}

	public void testThreadPool() {
		webServer.pollThreadPool();
	}
	
	public void testHandleRequest() throws IOException {
		URLConnection connection = new URL(URL).openConnection();
		connection.connect();
		BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = input.readLine();
        assertEquals(line, "<html>");
        input.close();
        connection = null;
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		webServer.shutdown();
		webServer = null;
	}	
}
