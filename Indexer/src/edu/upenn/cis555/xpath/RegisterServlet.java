package edu.upenn.cis555.xpath;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis555.db.User;
import edu.upenn.cis555.db.XPathDB;

public class RegisterServlet extends LoginServlet {
    private static final long serialVersionUID = 1L;

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
        
        out.print("<h2>Please Register");
        out.print("</h2><form action='/register' method='POST'>");
        out.print("<label for='fname'>First Name:</label>");
        out.print("<input type='text' name='fname' /><br />");
        out.print("<label for='lname'>Last Name:</label>");
        out.print("<input type='text' name='lname' /><br />");
        out.print("<label for='email'>Login (Email Address):</label>");
        out.print("<input type='text' name='email' /><br />");
        out.print("<label for='password'>Password:</label>");
        out.print("<input type='password' name='password' /><br />");
        out.print("<input type='submit' value='Register' />");
        out.print("</form></body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        String firstName = request.getParameter("fname");
        String lastName = request.getParameter("lname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<html><head><title>");
        out.print(session.getServletContext().getAttribute("ServletContext"));
        out.print("</title></head><body>");

        printNavigationLinks(user, out);
        
        if (firstName != null && lastName != null && email != null && password != null) {
            XPathDB database = getContextDatabase(session.getServletContext());
            User oldUser = database.getUser(email);
            if (oldUser != null) {
                out.print("<h2>A user is already registered with email '" + email + "'</h2>");
            }
            else {
                try {
                User newUser = new User(email, firstName, lastName, User.encrypt(password));
                database.addUser(newUser);
                out.print("<h2>Registration Successful</h2>");
                }
                catch (NoSuchAlgorithmException ex) {
                    out.print("<h2>Unexpected Error</h2>");
                }
            }
        }
        else {
            out.print("<h2>You must fill in all fields to register.</h2>");
        }
        
        out.print("</body></html>");
    }
}
