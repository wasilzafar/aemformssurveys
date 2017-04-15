package com.adobe.aemf.facilities.search;

public enum SearchItemType {
	FORMDATA(""),SURVEY(""), CRITICAL("");
	private String handler;

	private SearchItemType(String handler) {
		this.handler = handler;
	}
	
	String getDataHandler(){
		return handler;
	}

}
