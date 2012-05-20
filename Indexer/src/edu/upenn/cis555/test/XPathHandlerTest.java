package edu.upenn.cis555.test;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServlet;

import org.junit.Before;

import junit.framework.Assert;
import edu.upenn.cis555.webserver.HttpServletRequestImpl;
import edu.upenn.cis555.webserver.HttpServletResponseImpl;
import edu.upenn.cis555.xpath.XPathHandler;


public class XPathHandlerTest {
    HttpServlet servlet;
    HttpServletRequestImpl request;
    HttpServletResponseImpl response;
    ByteArrayOutputStream output;
    
    @Before
    public void setUp() throws Exception {
        output = new ByteArrayOutputStream();
        servlet = new XPathHandler();
        request = new HttpServletRequestImpl();
        request.setMethod("POST");
        response = new HttpServletResponseImpl(output, request);
    }
    
    @org.junit.Test public void xPathMatchesByUrl() {
        try {
            request.addParameter("xpath", "/note/heading");
            request.addParameter("xmlurl", "http://www.w3schools.com/XML/note.xml");
            servlet.service(request, response);
            response.getWriter().flush();
            Assert.assertTrue(output.toString().contains("Success"));
        }
        catch (Exception ex) {
            Assert.fail();
        }
    }
    
    @org.junit.Test public void xPathDoesNotMatchByUrl() {
        try {
            request.addParameter("xpath", "/web-app/servlet-mapping/servlet-name[@noSuchAttr = \"test\"]");
            request.addParameter("xmlurl", "http://www.w3schools.com/XML/note.xml");
            servlet.service(request, response);
            response.getWriter().flush();
            Assert.assertTrue(output.toString().contains("Failure"));
        }
        catch (Exception ex) {
            Assert.fail();
        }
    }
    
    @org.junit.Test public void xPathRssMatchesNestedTest() {
        try {
            request.addParameter("xpath", "/rss/channel[item[guid[@isPermaLink = \"false\"]]]");
            request.addParameter("xmlurl", "http://rss.cnn.com/rss/cnn_topstories.rss");
            servlet.service(request, response);
            response.getWriter().flush();
            Assert.assertTrue(output.toString().contains("Success"));
        }
        catch (Exception ex) {
            Assert.fail();
        }
    }
}
