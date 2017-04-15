package com.adobe.aemf.facilities.reporting;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * This class represents the abstract report. This could be extended for specific report types e.g. Excel, CSV etc
 * @author zafar
 *
 */
public abstract class Report {
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public abstract void disposeToHttpServletResponse(HttpServletResponse response) throws IOException;

}
