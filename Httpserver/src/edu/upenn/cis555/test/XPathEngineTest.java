package edu.upenn.cis555.test;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.w3c.dom.Document;

import edu.upenn.cis555.xpath.XPathEngine;

public class XPathEngineTest {
    Document document;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(new File("test.xml"));
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @org.junit.Test public void xPathMatches() {
        XPathEngine engine = new XPathEngine("/web-app/servlet/servlet-class");
        Assert.assertTrue(engine.evaluate(document));
    }

    @org.junit.Test public void xPathDoesNotMatch() {
        XPathEngine engine = new XPathEngine("/web-app/servlet/no-such-item");
        Assert.assertFalse(engine.evaluate(document));
    }
    
    @org.junit.Test public void simpleTextTest() {
        XPathEngine engine = new XPathEngine("/web-app/servlet-mapping/servlet-name[text() = \"cookie3\"]");
        Assert.assertTrue(engine.evaluate(document));
    }
    
    @org.junit.Test public void simpleContainsTest() {
        XPathEngine engine = new XPathEngine("/web-app/servlet-mapping/servlet-name[contains(text(),\"cook\")]");
        Assert.assertTrue(engine.evaluate(document));
    }
    
    @org.junit.Test public void simpleAttrTest() {
        XPathEngine engine = new XPathEngine("/web-app/servlet-mapping/servlet-name[@noSuchAttr = \"test\"]");
        Assert.assertFalse(engine.evaluate(document));
    }
    
    @org.junit.Test public void xPathMatchesByUrl() {
        try {
            XPathEngine engine = new XPathEngine("/note/heading");
            Assert.assertTrue(engine.evaluate("http://www.w3schools.com/XML/note.xml"));
        }
        catch (Exception ex) {
            Assert.fail();
        }
    }
    
    @org.junit.Test public void xPathDoesNotMatchByUrl() {
        try {
            XPathEngine engine = new XPathEngine("/web-app/servlet-mapping/servlet-name[@noSuchAttr = \"test\"]");
            Assert.assertFalse(engine.evaluate("http://www.w3schools.com/XML/note.xml"));
        }
        catch (Exception ex) {
            Assert.fail();
        }
    }
}
