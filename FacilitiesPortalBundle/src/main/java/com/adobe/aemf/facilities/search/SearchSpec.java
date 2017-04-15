package com.adobe.aemf.facilities.search;

import java.util.List;

public class SearchSpec {

	private SearchItemType type;

	public SearchItemType getType() {
		return type;
	}

	public void setType(SearchItemType type) {
		this.type = type;
	}

	public SearchSpec() {
	}

	private List<SearchItem> searchItems;

	public List<SearchItem> getSearchItems() {
		return searchItems;
	}

	public void addSearchItems(List<SearchItem> searchItems) {
		this.searchItems = searchItems;
	}

}
