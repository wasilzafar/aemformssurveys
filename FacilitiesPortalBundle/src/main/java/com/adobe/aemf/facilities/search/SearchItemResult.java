package com.adobe.aemf.facilities.search;

import java.util.Map;

public class SearchItemResult {
	boolean empty;
	Map data;
	public Map getData() {
		return data;
	}
	public void setData(Map result) {
		this.data = result;
	}
	public boolean isEmpty() {
		return empty;
	}
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	
}
