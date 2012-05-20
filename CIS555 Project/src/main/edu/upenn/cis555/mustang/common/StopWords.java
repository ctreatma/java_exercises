package edu.upenn.cis555.mustang.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StopWords {
	private static final String STOP_WORDS_LIST = "stopwords.lst";
	
	private static List<String> stopWords;
	
	 /**
	  * May always return false as search engines seem to not ignore these words as they used to. 
	  * @param word
	  * @return
	  */
	 public static boolean contains(String word) {
		 if (stopWords == null) {
			 stopWords = new ArrayList<String>();
			 BufferedReader input = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(STOP_WORDS_LIST)));            
		     String line;
		     try {
		    	 while ((line = input.readLine()) != null) {
		    		 stopWords.add(line);	
		    	 }
		     } catch (IOException e) { }
		 }
		 return stopWords.contains(word.toLowerCase());
	 }
	 
	 public static void main(String[] args) {
		 System.out.println("about: " + contains("about"));
		 System.out.println("test: " + contains("test"));
	 }
}
