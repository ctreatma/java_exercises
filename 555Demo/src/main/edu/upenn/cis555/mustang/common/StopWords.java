package edu.upenn.cis555.mustang.common;

import java.util.Arrays;
import java.util.List;

public class StopWords {
	 private static final List<String> STOP_WORDS = Arrays.asList( 
	    "a", "about", "add", "ago", "after", "all", "also", "an", "and", "another", "any", "are", "as", "at", "be", 
	    "because", "been", "before", "being", "between", "big", "both", "but", "by", "came", "can", "come", 
	    "could", "did", "do", "does", "due", "each", "else", "end", "far", "few", "for", "from", "get", "got", "had", 
	    "has", "have", "he", "her", "here", "him", "himself", "his", "how", "if", "in", "into", "is", "it", "its",
	    "just", "let", "lie", "like", "low", "make", "many", "me", "might", "more", "most", "much", "must", 
	    "my", "never", "no", "nor", "not", "now", "of", "off", "old", "on", "only", "or", "other", "our", "out", "over",
	    "per", "pre", "put", "re", "said", "same", "see", "she", "should", "since", "so", "some", "still", "such",
	    "take", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "those",
	    "through", "to", "too", "under", "up", "use", "very", "via", "want", "was", "way", "we", "well", "were", 
	    "what", "when", "where", "which", "while", "who", "will", "with", "would", "yes", "yet", "you", "your");
	 
	 /**
	  * Always returns false as search engines do not ignore these words as they used to. 
	  * @param word
	  * @return
	  */
	 public static boolean contains(String word) {
		 return STOP_WORDS.contains(word.toLowerCase());
	 }
}
