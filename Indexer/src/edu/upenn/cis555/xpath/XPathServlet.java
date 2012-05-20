package edu.upenn.cis555.xpath;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XPathServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String title = "XPathServlet";
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<html><head><title>");
        out.print(title);
        out.print("</title></head><body><h2>");
        out.print(title);
        out.print("</h2><form action='/handler' method='POST'>");
        out.print("<label for='xpath'>XPath Expression:</label>");
        out.print("<input type='text' name='xpath' /><br />");
        out.print("<label for='xmlurl'>URL to XML file:</label>");
        out.print("<input type='text' name='xmlurl' /><br />");
        out.print("<input type='submit' value='Submit' />");
        out.print("</form></body></html>");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
    }
}