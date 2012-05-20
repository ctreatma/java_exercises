package edu.upenn.cis555.mustang;

import junit.framework.TestSuite;

import edu.upenn.cis555.mustang.datastore.DataRepositoryTest;
import edu.upenn.cis555.mustang.peer.P2PServerTest;
import edu.upenn.cis555.mustang.search.RestHandlerTest;
import edu.upenn.cis555.mustang.web.WebSearchTest;
import edu.upenn.cis555.mustang.webserver.HttpserverTest;
import edu.upenn.cis555.mustang.webserver.QueueTest;

public class TestHarness {

	public static TestSuite suite() {
	    TestSuite suite = new TestSuite();
	    suite.addTestSuite(DataRepositoryTest.class);
	    suite.addTestSuite(P2PServerTest.class);
	    suite.addTestSuite(RestHandlerTest.class);
	    suite.addTestSuite(WebSearchTest.class);
	    suite.addTestSuite(HttpserverTest.class);
	    suite.addTestSuite(QueueTest.class);
	    return suite;
	}
	
}
