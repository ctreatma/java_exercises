package edu.upenn.cis555.xpath;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathEngine {
    private String xPathExpr;

    public XPathEngine(String xPathExpr) {
        this.xPathExpr = xPathExpr;
    }

    public boolean evaluate(String urlString) 
    throws MalformedURLException, IOException,
    ParserConfigurationException, SAXException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        String contentType = conn.getContentType();
        if (contentType.contains("xml")) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(conn.getInputStream());
            return evaluate(document);
        }
        else {
            throw new IllegalArgumentException("URL to be evaluated does not point to an XML file.");
        }
    }

    public boolean evaluate(Document document) {
        // Prepare the queue of tests
        String[] testStrs = xPathExpr.replaceFirst("/", "").split("/");
        Queue<String> tests = new LinkedList<String>();
        for (String test : testStrs) {
            tests.add(test);
        }

        return evaluateElement(document.getDocumentElement(), tests);
    }

    private boolean evaluateElement(Element element, Queue<String> tests) {
        // Two cases:
        //    1.  test is a tag name : if element has children matching tag name:
        //                                if has more tests:  test children
        //                                else return true
        //                             else return false
        //    2.  test is text(), contains(), or @attr : check element for match
        tests = new LinkedList<String>(tests);
        String test = tests.remove();

        // Must be a step
        // Get any subtests for the current step
        Queue<Queue<String>> subTests = null;
        if (test.contains("[")) {
            subTests = getSubTestQueue(test.substring(test.indexOf("[")));
            test = test.substring(0,test.indexOf("["));
        }
        if (element.getNodeName().compareTo(test) == 0) {
            boolean subTestsMatched = true;
            if (subTests != null) {
                for (Queue<String> subTest : subTests) {
                    subTestsMatched = subTestsMatched && evaluateSubtest(element, subTest);
                }
            }
            if (subTestsMatched) {
                if (tests.size() == 0) {
                    return true;
                }
                NodeList children = element.getChildNodes();
                for (int i = 0; i < children.getLength(); ++i) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        if (evaluateElement((Element) child, tests)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean evaluateSubtest(Element element, Queue<String> tests) {
        tests = new LinkedList<String>(tests);
        String test = tests.peek();

        // Check if this is a text() test
        Pattern pattern = Pattern.compile("^text\\(\\)\\s*=\\s*\"(.*)\"$");
        Matcher matcher = pattern.matcher(test);
        if (matcher.matches()) {
            test = matcher.group(1);
            return element.getTextContent() != null && element.getTextContent().compareTo(test) == 0;
        }

        // Check if this is a contains() test
        pattern = Pattern.compile("^contains\\(text\\(\\),\\s*\"(.*)\"\\)$");
        matcher = pattern.matcher(test);    
        if (matcher.matches()) {
            test = matcher.group(1);
            return element.getTextContent() != null && element.getTextContent().contains(test);
        }

        // Check if this is an @attr test
        pattern = Pattern.compile("^@([^\\s]+)\\s*=\\s*\"(.*)\"$");
        matcher = pattern.matcher(test);    
        if (matcher.matches()) {
            String attr = matcher.group(1);
            test = matcher.group(2);
            return element.hasAttribute(attr) && element.getAttribute(attr).compareTo(test) == 0;
        }

        boolean matched = false;
        test = test.substring(0,test.indexOf("["));
        NodeList children = element.getElementsByTagName(test);
        for (int i = 0; i < children.getLength(); ++i) {
            matched = matched || evaluateElement((Element)children.item(i), tests);
        }
        return matched;
    }
    
    private Queue<Queue<String>> getSubTestQueue(String xPathStep) {
        Queue<String> testStrings = new LinkedList<String>();
        Queue<Queue<String>> allTests = new LinkedList<Queue<String>>();

        // Break apart multiple bracketed conditions
        int numBrackets = 0;
        int startTest = 0;
        for (int i = 0; i < xPathStep.length(); ++i) {
            if (xPathStep.charAt(i) == '[') {
                if (numBrackets == 0) {
                    startTest = i + 1;
                }
                numBrackets++;
            }
            else if (xPathStep.charAt(i) == ']') {
                numBrackets--;
                if (numBrackets == 0) {
                    testStrings.add(xPathStep.substring(startTest, i));
                }
            }
        }

        // Split each test on / to create a new test queue
        for (String testString : testStrings) {
            String[] testStrs = testString.split("/");
            Queue<String> tests = new LinkedList<String>();
            for (String test : testStrs) {
                tests.add(test);
            }
            allTests.add(tests);
        }
        return allTests;
    }
}
