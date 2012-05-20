package edu.upenn.cis555.mustang.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis555.mustang.search.RankedDocument;
import edu.upenn.cis555.mustang.search.RestClient;
import edu.upenn.cis555.mustang.search.Searcher;
import edu.upenn.cis555.mustang.search.SubCollection;

public class WebSearch extends HttpServlet {
	private static final String SEARCH_ENGINE_HOST_CONTEXT_PARAM = "searchEngine";
	private static final String SEARCH_ENGINE_PORT_CONTEXT_PARAM = "searchEnginePort";
	private static final String REST_WEB_SERVICE_URI = "/search/";
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		renderFormPage(response, null, null, null, null);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		String keyword = request.getParameter("query");
		if (keyword == null || keyword.trim().equalsIgnoreCase("")) {
			renderFormPage(response, keyword, "", null, null);
		} else {
			String host = getServletConfig().getInitParameter(SEARCH_ENGINE_HOST_CONTEXT_PARAM);
			int port = Integer.parseInt(getServletConfig().getInitParameter(SEARCH_ENGINE_PORT_CONTEXT_PARAM));
			RestClient client = new RestClient(host, port);
			try {
				long start = System.currentTimeMillis();
				SubCollection<RankedDocument> results = client.connect("http://" + host + ":" + port + REST_WEB_SERVICE_URI + 
					URLEncoder.encode(keyword, "UTF-8"));
				String elapsed = " (" + String.valueOf(0.001f * (System.currentTimeMillis() - start)) + " seconds)" ;
				renderFormPage(response, keyword, null, results, elapsed);
			} catch (IOException e) {
				renderFormPage(response, keyword, "Gateway/P2P down - " + e.getMessage(), null, null);
			}
		}
	}
	
	private void renderFormPage(HttpServletResponse response, String query, 
		String validation, SubCollection<RankedDocument> results, String elapsed) throws IOException {
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Search</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h2>CIS-555 Spring 2010 MuSTanG Project</h2>");
		out.println("<div style=\"margin-bottom: 25px;\">" + (validation == null? "" : validation) + "</div>");
		out.println("<form method=\"POST\" action=\"search\">");
		out.println("<table><tr><td>");
		out.println("<input style=\"width: 200px;\" id=\"query\" name=\"query\" type=\"text\" value='" + (query == null? "" : query) + "'/>");
		out.println("</td><td><br></td><td><input type=\"submit\" value=\"Search\"/></td></tr></table>");
		out.println("</form>");
		if (results != null) {
			if ("".equals(results.getIgnoredWords())) {
				out.println("<hr><div style=\"float: left;\"><h3>Web Search</h3></div>");				
			} else {
				out.println("<hr><div style=\"float: left;\"><h3>Web Search<span style=\"font-family: times, serif; font-size:14px; font-style:italic; font-weight:normal;\"> (common word(s) " + results.getIgnoredWords() + " were ignored)</span></h3></div>");				
			}
			if (results.getTotalSize() == 0) {
				out.println("<div style=\"clear: both;\"></div><div>No results matching any documents</div>");
			} else {
				if (results.getTotalSize() > Searcher.PAGING_LIMIT) {
					out.println("<div style=\"float: right;\">Results 1 - " + Searcher.PAGING_LIMIT + " of " + results.getTotalSize() + elapsed + "</div>");
				} else {
					if (results.getTotalSize() == 1) {
						out.println("<div style=\"float: right;\">Result 1 of " + results.getTotalSize() + elapsed + "</div>");
					} else {
						out.println("<div style=\"float: right;\">Results 1 - " + results.getTotalSize() + " of " + results.getTotalSize() + elapsed + "</div>");
					}
				}
				out.println("<div style=\"clear: both;\"></div><ul style=\"list-style-type: none; margin: -5px 0px 0px 0px; padding-left: 0px;\">");
				for (RankedDocument result : results.getSubCollection()) {
					out.println("<li><div style=\"float: left;\">" + "<a target=\"_blank\" href=\"" + result.getUrl() + "\">" + result.getTitle() + "</a></div>");
					out.println("<div style=\"float: right;\"><em>" + result.getRank().getScore() + "</em></div>"); 
					out.println("<div style=\"clear: both;\"></div>");
					out.println("<div>" + highlightQueryTerms(result.getBlurb(), query) + "</div></li><br>");
				}
				out.println("</ul>");
			}
		}
		out.println("</body>");
		out.println("</html>");
	}
	
	private String highlightQueryTerms(final String blurb, final String query) {
		String highlight = blurb;
		List<String> terms = Arrays.asList(query.split("\\W+"));
		for (String term : terms) {
			highlight = highlight.replaceAll("(?i)\\b" + term + "\\b", "<strong>" + term + "</strong>");
		}
		return highlight;
	}
}
