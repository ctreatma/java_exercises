package edu.upenn.cis555.mustang.search;

import java.io.IOException;

import junit.framework.TestCase;

import edu.upenn.cis555.mustang.common.SearchResult;

/**
 *  Tests web service REST requesting 
 */
public class RestHandlerTest extends TestCase {

	public void testHandleRequest() throws IOException {
		String line = "GET /search/foobar HTTP/1.1";
		Request request = new RestHandler().handleRequest(line);
		assertEquals("Query in REST request is foobar.", request.getQuery(), "foobar");
	}
	
	public void testHandleResponse() throws IOException {
		Response response = new Response();
		response.setQuery("foobar");
		response.setResultCount(1);
		RankedDocument document = new RankedDocument(new SearchResult("Foo", "http://www.foo.bar", "baz"));
		DocumentRank rank = new DocumentRank();
		rank.setContextHint(1);
		rank.setProximity(1.5);
		rank.setSimilarity(0.5);
		document.setRank(new DocumentRank());
		response.addResult(document);
		String xml = new RestHandler().handleResponse(response);
		assertTrue("REST response XML contains <title>Foo</title>.", xml.contains("<title>Foo</title>"));
		assertTrue("REST response XML contains <blurb>baz</blurb>.", xml.contains("<blurb>baz</blurb>"));
		assertTrue("REST response XML contains <url>http://foo.bar</url>.", xml.contains("<url>http://www.foo.bar</url>"));
		assertTrue("REST response XML contains <rank>0.020999999999999998</rank>.", xml.contains("<rank>0.020999999999999998</rank>"));
	}
}
