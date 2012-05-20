package edu.upenn.cis555.xpath;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis555.db.Channel;
import edu.upenn.cis555.db.User;
import edu.upenn.cis555.db.XPathDB;

public class ShowChannelsServlet extends LoginServlet {
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
        
        out.print("<h2>All Channels</h2>");
        
        XPathDB database = getContextDatabase(session.getServletContext());
        ArrayList<Channel> channels = database.getAllChannels();
        for (Channel channel : channels) {
            out.print("<p>" + channel.getName() + ":&nbsp;<a href='/showChannel?channel=");
            out.print(URLEncoder.encode(channel.getName(), response.getCharacterEncoding()));
            out.print("'>view</a>");
            if (user != null && user.getEmail().compareTo(channel.getCreatedByEmail()) == 0) {
                out.print("&nbsp;|&nbsp;<a href='/deleteChannel?channel=");
                out.print(URLEncoder.encode(channel.getName(), response.getCharacterEncoding()));
                out.print("'>delete</a>");
            }
            out.print("</p><br />");
        }
        
        out.print("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
