package edu.upenn.cis555.xpath;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis555.db.Channel;
import edu.upenn.cis555.db.CrawledURL;
import edu.upenn.cis555.db.XPathDB;

public class ShowChannelServlet extends LoginServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        String channelName = request.getParameter("channel");
        
        XPathDB database = getContextDatabase(session.getServletContext());
        Channel channel = database.getChannel(channelName);
        ArrayList<CrawledURL> crawledUrls = database.getAllCrawledURLs();
        
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        
        out.print("<?xml version='1.0' encoding='" + response.getCharacterEncoding() + "'?>");
        out.print("<?xml-stylesheet href='" + channel.getStyleSheetUrl() + "' type='text/xsl' ?>");
        out.print("<documentcollection>");
        
        for (CrawledURL crawledUrl : crawledUrls) {
            if (crawledUrl.getChannelNames() != null
                    && crawledUrl.getChannelNames().contains(channel.getName())) {
                out.print("<document");
                out.print(" crawled='" + format.format(crawledUrl.getLastCrawled()) + "'");
                out.print(" location='" + crawledUrl.getUrl() + "' >");
                
                out.print(crawledUrl.getContent());
                    
                out.print("</document>");
            }
        }

        out.print("</documentcollection>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
