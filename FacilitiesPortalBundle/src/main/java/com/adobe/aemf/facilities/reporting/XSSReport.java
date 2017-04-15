package com.adobe.aemf.facilities.reporting;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
public class XSSReport extends Report {

	XSSFWorkbook workbook;
	public XSSReport(XSSFWorkbook workbook2) {
		this.workbook = workbook2;
	}
	@Override
	public void disposeToHttpServletResponse(HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition",
				"attachment; filename="+getName());
		workbook.write(response.getOutputStream());
	}

}
