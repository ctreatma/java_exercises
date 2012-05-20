package edu.upenn.cis555.test;

import org.junit.After;
import org.junit.Before;

import edu.upenn.cis555.db.XPathDB;
import edu.upenn.cis555.xpath.XPathCrawler;

import junit.framework.Assert;
import junit.framework.TestCase;

public class XPathCrawlerTest extends TestCase {
    XPathDB database;
    XPathCrawler crawler;

    @Before
    public void setUp() throws Exception {
        database = new XPathDB("TestDB");
    }

    @After
    public void tearDown() throws Exception {
        database.close();
    }

    @org.junit.Test public void testCrawlerStopsAtZeroVisited() throws Exception {
        crawler = new XPathCrawler(database, 1, 0); 
        crawler.crawl("http://rss.cnn.com/rss/cnn_topstories.rss");
        Assert.assertTrue(crawler.getNumVisited() == 0);
    }
    
    @org.junit.Test public void testCrawlerRespectsZeroMaxSize() throws Exception {
        crawler = new XPathCrawler(database, 0, 200); 
        crawler.crawl("http://www.cnn.com/services/rss/");
        Assert.assertTrue(crawler.getNumVisited() == 0);
    }
}
