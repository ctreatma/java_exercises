package edu.upenn.cis555.mustang.index;

import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.NodeReader;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;

public class Extractor {
    public String extractStrings() throws ParserException, IOException {
//    	sb.setLinks(false);
    	String inputHTML = "<html><head><title>Test</title><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body><h2>Text</h2><div<a href=\"../string.html\">\"String\" Test.</a></div></body></html>";
//        Parser parser = new Parser(new NodeReader(new StringReader(inputHTML), ""));
    	Parser parser = new Parser();
    	parser.setReader(new NodeReader(new StringReader(inputHTML), ""));
        TextBean sb = new TextBean();
//        sb.setReplaceNonBreakingSpaces(true);
//        sb.setCollapse(true);
//       sb.setLinks(false);
        sb.setParser(parser);
/*        String ret;
        parser.flushScanners();
        parser.registerScanners();
        StringBuffer buffer = new StringBuffer(4096);
        parser.visitAllNodesWith(sb);
        ret = buffer.toString(); */
//        parser.visitAllNodesWith(sb);
//        String s = sb.getStrings();
//        sb.setLinks(true);
//        parser.getReader().reset();
//        parser.visitAllNodesWith(sb);
        String sl = sb.getStrings();
System.out.println(sl);
        StringTokenizer tokens = new StringTokenizer(sl);
        while (tokens.hasMoreTokens()) {
        	String token = tokens.nextToken().replaceAll("[^\\w\\-']", "");
System.out.println("token: " + token + " - title: " + isHeader(token, inputHTML) + " - anchor: " + isAnchor(token, inputHTML));
        }
        String title = extractTitle(inputHTML);
System.out.println("Page title: " + title);
		String sentence = extractSentence(sl);
System.out.println("Page blurb: " + sentence);		
        return sl;
    }

	private boolean isHeader(String word, String page) {
		String title = "(?i)<head>.*<title>.*\\b" + word + "\\b.*</title>.*</head>";
		Pattern pattern = Pattern.compile(title);
        Matcher matcher = pattern.matcher(page);
		return matcher.find();
	}
	
	private boolean isAnchor(String word, String page) {
		String anchor = "(?i)<a\\b.*href=\"([^\"]*)\".*>.*\\b" + word + "\\b.*</a\\s*>";
		Pattern pattern = Pattern.compile(anchor);
		Matcher matcher = pattern.matcher(page);
		return matcher.find();
	}
    
	private String extractTitle(String page) {
		String title = null;
        Pattern pattern = Pattern.compile("(?i)<head>.*<title>(.*)</title>.*</head>");
        Matcher matcher = pattern.matcher(page);
		if (matcher.find()) {
			title = matcher.group(matcher.groupCount());
		}	
		return title;
	}
/*	
	private String extractSentence(String page) {
        Pattern pattern = Pattern.compile("(?i)<body>.+</body>");
        Matcher matcher = pattern.matcher(page);
		String sentence = matcher.find() ? matcher.group(0) : page;
System.out.println("sentence 1: " + sentence);		
        pattern = Pattern.compile("</?.+>??");
        matcher = pattern.matcher(sentence);
        sentence = matcher.replaceAll("");
System.out.println("sentence 2: " + sentence);        
		pattern = Pattern.compile("(?i)[^\\.\\?!]*\\b" + "test" + "\\b\\[^\\.\\?!]+[\\.\\?!]");
		matcher = pattern.matcher(sentence);
		// seek the beginning of sentence containing the word
		if (matcher.find()) {
			sentence = matcher.group(0);
		}
System.out.println("sentence 3: " + sentence);	
		return sentence;
	}
*/	
	private String extractSentence(String text) {
//		Pattern pattern = Pattern.compile("(?i)<body>.+</body>");
//        Matcher matcher = pattern.matcher(page);
//		String text = matcher.find() ? matcher.group(0) : page;
		StringBuilder buffer = new StringBuilder();
		int position = 0;
        StringTokenizer tokens = new StringTokenizer(text);
        while (tokens.hasMoreTokens()) {
        	String token = tokens.nextToken().replaceAll("[^\\w\\-']", "");
        	position++;
        	if ("test".equalsIgnoreCase(token)) {
        		 break;
        	}
        }
        int window = position > 0 ? 10 : 20; 
    	int displacement = 0;
        tokens = new StringTokenizer(text);
        while (tokens.hasMoreTokens()) {
        	String token = tokens.nextToken().replaceAll("[^\\w\\-']", "");
        	displacement++;
        	if (Math.abs(position - displacement) < window) {
        		buffer.append(token).append(" ");
        	}
        }
        return buffer.toString();
	}
	
    class TextBean extends StringBean {
    	public void setParser(Parser parser) {
    		mParser = parser;
    	}
    }
    
    /**
     * Mainline.
     * @param args The command line arguments.
     * @throws ParserException 
     * @throws IOException 
     */
    public static void main(String[] args) throws ParserException, IOException {
    	new Extractor().extractStrings();
    	String[] keywords = "crawler + index_pagerank".split("\\W+");
    	for (String keyword : keywords) {
    		System.out.println(keyword);
    	}
	}
}
