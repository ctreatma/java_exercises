package edu.upenn.cis555.xpath;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XPathHandler extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String title = "XPathHandler";
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String xPath = request.getParameter("xpath");
        String xmlUrl = request.getParameter("xmlurl");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<html><head><title>");
        out.print(title);
        out.print("</title></head><body>");
        
        try {
            XPathEngine engine = new XPathEngine(xPath);
            if (engine.evaluate(xmlUrl)) {
                out.print("<h2>Success</h2>");
                out.print("<p>The input xpath '" + xPath + "' matched the requested file.</p>");
            }
            else {
                out.print("<h2>Failure</h2>");
                out.print("<p>The input xpath '" + xPath + "' did not match the requested file.</p>");
            }
        }
        catch (Exception ex) {
            out.print("<h2>Error</h2>");
            out.print("<p>" + ex.getMessage() + "</p>");
        }
        
        out.print("</body></html>");
    }

}
