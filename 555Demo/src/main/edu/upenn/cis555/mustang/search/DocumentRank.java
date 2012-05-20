package edu.upenn.cis555.mustang.search;

public class DocumentRank {
    private static final double PAGE_RANK_WEIGHT = 0.3;
    private static final double SCORE_WEIGHT = 0.7;
//	private String docId;
	private double similarity;
	private int contextHint;
	private double proximity;
	private double score;
	private double pageRank;
/*	
	String getDocId() {
		return docId;
	}

	void setDocId(String docId) {
		this.docId = docId;
	}
*/
	double getSimilarity() {
		return similarity;
	}
	
	void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	
	int getContextHint() {
		return contextHint;
	}
	
	void setContextHint(int contextHint) {
		this.contextHint = contextHint;
	}
	
	double getProximity() {
		return proximity;
	}
	
	void setProximity(double proximity) {
		this.proximity = proximity;
	}
	
	void setPageRank(double pageRank) {
	    this.pageRank = pageRank;
	}
	
	double getPageRank() {
	    return pageRank;
	}

	public double getScore() {
		if (score == 0) {
			score = getSimilarity() * 0.5 + 0.1 / (1.0 + getProximity())* 0.3 +
				Math.log1p(getContextHint()) * 0.2;
			score = (DocumentRank.SCORE_WEIGHT * score) + (DocumentRank.PAGE_RANK_WEIGHT * getPageRank());
		}
		return score;
	}

	void setScore(double score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "similarity = " + getSimilarity() + ", proximity = " + getProximity() + 
			", context hint = " + getContextHint() + ", rank = " + getScore() ;
	}
}
