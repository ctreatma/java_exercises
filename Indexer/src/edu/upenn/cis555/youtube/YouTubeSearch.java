package edu.upenn.cis555.youtube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class YouTubeSearch extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("youTubeSearch.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("k");
        if (keyword == null || keyword.trim().length() == 0) {
            doGet(request,response);
        }
        else {
            ServletConfig config = getServletConfig();
            String cacheServer = config.getInitParameter("cacheServer");
            String cacheServerPort = config.getInitParameter("cacheServerPort");
            
            URL url = new URL("http://" + cacheServer + ":" + cacheServerPort + PastryThread.REST_PATH + URLEncoder.encode(keyword, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    out.println(line);
                }
            }
            else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    out.println(line);
                } // TODO : Do something else here?  Generic error page?
            }
        }
    }

    
}
