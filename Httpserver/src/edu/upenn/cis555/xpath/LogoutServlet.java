package edu.upenn.cis555.xpath;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis555.db.User;

public class LogoutServlet extends LoginServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.print("<html><head><title>");
        out.print(request.getSession().getServletContext().getAttribute("ServletContext"));
        out.print("</title></head><body>");
        
        printNavigationLinks(null, out);
        
        if (user != null) {
            session.setAttribute("user", null);
            session.invalidate();
            out.print("<h2>Successfully logged out</h2>");
        }
        else {
            out.print("<h2>You have to <a href='/login'>log in</a> to log out.</h2>");
        }

        out.print("</body></html>");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
    }


}
