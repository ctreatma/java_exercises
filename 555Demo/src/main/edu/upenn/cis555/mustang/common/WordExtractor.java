package edu.upenn.cis555.mustang.common;

import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;

public class WordExtractor extends StringBean {
	
	public WordExtractor(Parser parser) {
		setParser(parser);
    	setLinks(true);
	}
    	
    private void setParser(Parser parser) {
    	mParser = parser;
    }
    
}
