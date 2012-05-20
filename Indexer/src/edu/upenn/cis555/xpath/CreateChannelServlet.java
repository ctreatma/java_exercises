package edu.upenn.cis555.xpath;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis555.db.Channel;
import edu.upenn.cis555.db.User;
import edu.upenn.cis555.db.XPathDB;

public class CreateChannelServlet extends LoginServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<html><head><title>");
        out.print(session.getServletContext().getAttribute("ServletContext"));
        out.print("</title></head><body>");

        printNavigationLinks(user, out);
        
        if (user != null) {
            // If a get, show create channel form if user is logged in
            out.print("<h2>Create Channel</h2><form action='/createChannel' method='POST'>");
            out.print("<label for='name'>Channel Name:</label>");
            out.print("<input type='text' name='name' /><br />");
            out.print("<label for='xpath'>XPaths:</label>");
            out.print("<textarea name='xpath'>Enter XPaths on separate lines.</textarea><br />");
            out.print("<label for='stylesheet'>Stylesheet URL:</label>");
            out.print("<input type='text' name='stylesheet' /><br />");
            out.print("<input type='submit' value='Create Channel' />");
            out.print("</form>");
        }
        else {
            out.print("<h2>You have to <a href='/login'>log in</a> to create a channel.</h2>");
        }
        
        out.print("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<html><head><title>");
        out.print(session.getServletContext().getAttribute("ServletContext"));
        out.print("</title></head><body>");

        printNavigationLinks(user, out);
        
        if (user != null) {
            String name = request.getParameter("name");
            String stylesheet = request.getParameter("stylesheet");
            String xpath = request.getParameter("xpath");
            if (name != null && xpath != null) {
                String[] xpathStrs = xpath.split("[\\r\\n]+");
                XPathDB database = getContextDatabase(session.getServletContext());
                Channel oldChannel = database.getChannel(name);
                if (oldChannel != null) {
                    out.print("<h2>A channel named '" + name +"' already exists.</h2>");
                }
                else {
                    ArrayList<String> xpaths = new ArrayList<String>();
                    for (String xpathStr : xpathStrs) {
                        xpaths.add(xpathStr);
                    }
                    Channel channel = new Channel(name, xpaths, stylesheet, user.getEmail());
                    database.addChannel(channel);
                    out.print("<h2>The channel '" + name +"' was created.</h2>");
                }
            }
            else {
                out.print("<h2>All fields are required to create a channel.</h2>");
            }
        }
        else {
            out.print("<h2>You have to <a href='/login'>log in</a> to create a channel.</h2>");
        }
        
        out.print("</body></html>");
    }

}
