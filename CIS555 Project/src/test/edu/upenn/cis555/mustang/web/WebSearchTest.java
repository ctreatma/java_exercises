package edu.upenn.cis555.mustang.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.junit.Ignore;

import edu.upenn.cis555.mustang.webserver.HttpserverTest;
import edu.upenn.cis555.mustang.webserver.support.WebUtil;

public class WebSearchTest extends HttpserverTest {
	public WebSearchTest(String testName) {
		super(testName);
	}
	
	public void testServerRunning() {
		assertEquals(WebUtil.getHttpServlet("/search").getServletName(), "webSearch");
	}

	@Ignore
	public void testThreadPool() {
	}
	
	@Ignore
	public void testHandleRequest() throws IOException {
	}
	
	public void testGet() throws ServletException, IOException {
		HttpServlet servlet = WebUtil.getHttpServlet("/search");
		HttpServletRequestMock request = new HttpServletRequestMock();
		request.setMethod("GET");
		HttpServletResponseMock response = new HttpServletResponseMock();
		response.setContentType("text/html");
		servlet.service(request, response);
		assertTrue(response.getContent().contains("MuSTanG"));
		assertTrue(response.getContent().contains("POST"));
	}

	public void testPost() throws ServletException, IOException {
		HttpServlet servlet = WebUtil.getHttpServlet("/search");
		HttpServletRequestMock request = new HttpServletRequestMock();
		request.setParameter("query", "foo bar");
		request.setMethod("POST");
		HttpServletResponseMock response = new HttpServletResponseMock();
		response.setContentType("text/html");
		servlet.service(request, response);
		assertTrue(response.getContent().contains("Gateway/P2P down"));
	}
}
