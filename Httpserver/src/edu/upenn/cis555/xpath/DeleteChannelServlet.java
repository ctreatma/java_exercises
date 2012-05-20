package edu.upenn.cis555.xpath;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis555.db.Channel;
import edu.upenn.cis555.db.User;
import edu.upenn.cis555.db.XPathDB;

public class DeleteChannelServlet extends LoginServlet {
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
            String name = request.getParameter("channel");
            XPathDB database = getContextDatabase(session.getServletContext());
            Channel channel = database.getChannel(name);
            if (user.getEmail().compareTo(channel.getCreatedByEmail()) == 0) {
                database.removeChannel(channel);
                out.print("<h2>You have successfully deleted the channel '" + channel.getName() + "'</h2>");
            }
            else {
                out.print("<h2>You did not create the channel you're trying to delete.</h2>");
            }
        }
        else {
            out.print("<h2>You have to <a href='/login'>log in</a> to delete a channel.</h2>");
        }    

        out.print("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
