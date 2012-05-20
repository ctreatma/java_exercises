package edu.upenn.cis555.mustang.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HitList implements Serializable {
	private boolean header;
	private boolean anchor;
	private List<Integer> positions;

	public HitList() {
		positions = new ArrayList<Integer>();
	}
	
	public List<Integer> getPositions() {
		return positions;
	}

	void addPosition(int position) {
		positions.add(position);
	}
	
	public boolean isHeader() {
		return header;
	}

	void setHeader(boolean header) {
		this.header = header;
	}

	public boolean isAnchor() {
		return anchor;
	}

	void setAnchor(boolean anchor) {
		this.anchor = anchor;
	}	
}
