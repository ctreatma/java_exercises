package edu.upenn.cis555.mustang.peer;

import java.io.IOException;
import java.net.InetAddress;

import junit.framework.TestCase;
import edu.upenn.cis555.mustang.search.RankedDocument;
import edu.upenn.cis555.mustang.search.RestClient;
import edu.upenn.cis555.mustang.search.SubCollection;

/**
 *  Tests caching / storage system 
 */
public class P2PServerTest extends TestCase {
	private static final int BOOTSTRAP_PORT = 9001;
	private static final int GATEWAY_PORT = 8108;
	private static final String TEST_DATA = "target/testdata";
	
	private P2PServer p2pServer;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		p2pServer = new P2PServer(null, BOOTSTRAP_PORT, GATEWAY_PORT, TEST_DATA);
		p2pServer.launch();
		p2pServer.startSearcher();
	}
	
	public void testSearchGateway() throws IOException {
		String host = InetAddress.getLocalHost().getHostName();
		RestClient restClient = new RestClient(host, GATEWAY_PORT);
		SubCollection<RankedDocument> documents = restClient.connect("http://" + host + ":" + GATEWAY_PORT + "/search/foobar");
		assertTrue("Search on foobar did not match any documents.", documents.getSubCollection().isEmpty());
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		p2pServer.shutdown();
		p2pServer = null;
	}	
}
