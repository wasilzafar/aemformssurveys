package com.adobe.aemf.facilities.search;

import java.util.Map;


public class SearchItem {
	public enum SearchScope {
		ALL,LIMITED
	}
	private SearchScope scope;
	private SearchItemResult searchResult;
	private Map<String, String> filter;
	public SearchScope getScope() {
		return scope;
	}

	public void setScope(SearchScope scope) {
		this.scope = scope;
	}

	public SearchItemResult getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(SearchItemResult searchResult) {
		this.searchResult = searchResult;
	}

	public Map<String, String> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, String> filter) {
		this.filter = filter;
	}
	
}
