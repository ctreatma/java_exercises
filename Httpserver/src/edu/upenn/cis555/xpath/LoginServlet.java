package edu.upenn.cis555.xpath;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis555.db.User;
import edu.upenn.cis555.db.XPathDB;

public class LoginServlet extends HttpServlet {
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
        
        printNavigationLinks(user, out);
        
        if (user == null) {
            out.print("<h2>Please Log In</h2><form action='/login' method='POST'>");
            out.print("<label for='email'>Login (Email Address):</label>");
            out.print("<input type='text' name='email' /><br />");
            out.print("<label for='password'>Password:</label>");
            out.print("<input type='password' name='password' /><br />");
            out.print("<input type='submit' value='Log In' />");
            out.print("</form>");
        }
        else {
            out.print("<p>You are logged in as " + user.getFirstName() + " " + user.getLastName() + "</p>");
            out.print("<a href='/logout'>Log out</a>");
        }
        
        out.print("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<html><head><title>");
        out.print(session.getServletContext().getAttribute("ServletContext"));
        out.print("</title></head><body>");

        if (email != null && password != null && session.getAttribute("user") == null) {
            try {
            XPathDB database = getContextDatabase(session.getServletContext());
            User user = database.getUser(email);
            password = User.encrypt(password);
            if (user != null && user.getPassword().compareTo(password) == 0) {
                session.setAttribute("user", user);
                printNavigationLinks(user, out);
                out.println("<h2>Login successful</h2>");
            }
            else if (user == null) {
                printNavigationLinks(user, out);
                out.print("<h2>No user exists for email '" + email + ".'  Please <a href='/register'>register</a>.</h2>");
            }
            else {
                printNavigationLinks(user, out);
                out.print("<h2>Incorrect password.</h2>");
            }
            }
            catch (NoSuchAlgorithmException ex) {
                out.print("<h2>Unexpected Error</h2>");
            }
        }
        else {
            printNavigationLinks(null, out);
            out.print("<h2>All fields are required to log in.</h2>");
        }
        
        out.print("</body></html>");
    }

    XPathDB getContextDatabase(ServletContext context) throws IOException {
        XPathDB database = (XPathDB) context.getAttribute("BDBstore");
        if (database == null) {
            database = new XPathDB(context.getInitParameter("BDBstore"));
            context.setAttribute("BDBstore", database);
        }
        return database;
    }
    
    void printNavigationLinks(User user, PrintWriter out) {
        out.print("<table><tr>");
        if (user != null) {
            out.print("<td colspan=4>Logged in as: " + user.getFirstName() + " " + user.getLastName() + "</td>");
        }
        else {
            out.print("<td colspan=4>Not logged in.</td>");
        }
        out.print("</tr><tr>");
        out.print("<td><a href='/showChannels'>All Channels</a>&nbsp;|&nbsp;</td>");
        out.print("<td><a href='/createChannel'>Create A Channel</a>&nbsp;|&nbsp;</td>");
        out.print("<td><a href='/register'>Register</a>&nbsp;|&nbsp;</td>");
        if (user == null) {
            out.print("<td><a href='/login'>Log In</a>&nbsp;|&nbsp;</td>");
        }
        else {
            out.print("<td><a href='/logout'>Log Out</a></td>");
        }
        out.print("</tr></table>");
    }
}
