package edu.upenn.cis555.webserver;

import java.io.File;

import javax.servlet.http.*;

import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import edu.upenn.cis555.webserver.*;

public class HttpserverTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @org.junit.Test public void requestHasNoSession() {
        HttpServletRequestImpl request = new HttpServletRequestImpl();
        HttpSession session = request.getSession(false);
        Assert.assertTrue(session == null);
    }

    @org.junit.Test public void requestAllowsMultipleParams() {
        HttpServletRequestImpl request = new HttpServletRequestImpl();
        request.addParameter("testParam", "testValue1");
        request.addParameter("testParam", "testValue2");
        request.addParameter("testParam", "testValue3");
        Assert.assertTrue(request.getParameterValues("testParam").length == 3);
    }
    
    @org.junit.Test public void responseNotCommitted() {
        HttpServletResponseImpl response = new HttpServletResponseImpl(null, null);
        Assert.assertFalse(response.isCommitted());
    }
    
    @org.junit.Test public void sessionIdsUnique() {
        HttpSessionImpl session1 = new HttpSessionImpl();
        HttpSessionImpl session2 = new HttpSessionImpl();
        Assert.assertFalse(session1.getId().equalsIgnoreCase(session2.getId()));
    }
    
    @org.junit.Test public void configCanAddInitParam() {
        ServletConfigImpl config = new ServletConfigImpl(null, null);
        config.setInitParam("testName", "testValue");
        Assert.assertTrue(config.getInitParameterNames().nextElement().toString().compareTo("testName") == 0);
    }
    
    @org.junit.Test public void contextGetsRealPath() {
        File test = new File("/");
        ServletContextImpl context = new ServletContextImpl(null, test);
        String realPath = context.getRealPath("/test");
        String testPath = null;
        try {
            testPath = new File("/test").getCanonicalPath();
        }
        catch (Exception ex) {
            
        }
        Assert.assertTrue(realPath.equalsIgnoreCase(testPath));
    }
}
