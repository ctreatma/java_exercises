package edu.upenn.cis555.mustang.search;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis555.mustang.common.Queue;
import edu.upenn.cis555.mustang.common.SearchResult;
import edu.upenn.cis555.mustang.index.DistributedHashKey;
import edu.upenn.cis555.mustang.peer.IndexApp;

public class Searcher implements Runnable {
	public static final int PAGING_LIMIT = 10; 
	
	private Queue<Socket> queue;
	private IndexApp indexApp;
	private Ranker ranker; 
	
	public Searcher(Queue<Socket> queue, DistributedHashKey hashKey, IndexApp indexApp) {
		this.indexApp = indexApp;
		this.queue = queue;
		ranker = new Ranker(hashKey, indexApp);
	}
	
	public void run() {
		while (true) {
    		Socket socket = queue.dequeue();
    		if (indexApp == null || socket == null) {
    			break;
    		}
	        try {
	        	BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));            
		        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		        RestHandler handler = new RestHandler();
		        String line = null;
		        Request request = null;
		        while (!socket.isClosed() && socket.isConnected() && (line = input.readLine()) != null && line.length() > 0) {
		        	request = handler.handleRequest(line);
		        }
		        List<String> keywords = Arrays.asList(request.getQuery().toLowerCase().split("\\W+"));
		        List<RankedDocument> rankedDocs = ranker.rank(keywords);
		        int count = 0;
		        Response response = new Response();
		        for (RankedDocument doc: rankedDocs) {
		        	count++;
		        	RankedDocument result = new RankedDocument(new SearchResult(doc.getTitle(), doc.getUrl(), doc.getBlurb()));
		        	result.setRank(doc.getRank());
		        	response.addResult(result);
		        	if (count > PAGING_LIMIT) {
		        		break;
		        	}
		        }
	        	response.setQuery(request.getQuery());
	        	response.setResultCount(rankedDocs.size());
				output.writeBytes(RestClient.HTTP_VERSION + " " + HttpServletResponse.SC_OK + " " + "OK" + RestClient.LINE_SEPARATOR);
				output.writeBytes("Content-Type" + ": " + RestClient.DEFAULT_CONTENT_TYPE + RestClient.LINE_SEPARATOR);
				output.writeBytes(handler.handleResponse(response));
				output.writeBytes(RestClient.LINE_SEPARATOR);
				output.flush();
				output.close();
	        } catch (IOException e) { }
    	}
	}
}
