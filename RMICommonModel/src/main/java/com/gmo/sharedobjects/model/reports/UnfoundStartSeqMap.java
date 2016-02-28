package com.gmo.sharedobjects.model.reports;

import java.io.Serializable;
import java.util.HashMap;

public class UnfoundStartSeqMap extends HashMap<String, Integer> implements Serializable {
	
	int totalCount;

	public UnfoundStartSeqMap() {
		super();
		totalCount = 0;
	}
	
	@Override
	public Integer put(String key, Integer value) {
		Integer previous = super.put(key, value);
		if(previous == null) {
			totalCount += value;
		} else {
			totalCount += (value - previous);
		}
		return previous;
	}

	public int getTotalCount() {
		return totalCount;
	}
	
}
