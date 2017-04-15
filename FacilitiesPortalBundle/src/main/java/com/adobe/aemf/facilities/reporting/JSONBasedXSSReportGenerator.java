package com.adobe.aemf.facilities.reporting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.ChartLegend;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.LegendPosition;
import org.apache.poi.ss.usermodel.charts.LineChartData;
import org.apache.poi.ss.usermodel.charts.LineChartSeries;
import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemf.facilities.core.PortalConfigComponent;
import com.adobe.aemf.facilities.core.PortalDataAccessManager;
import com.adobe.aemf.facilities.exceptions.PortalException;
import com.adobe.aemf.facilities.search.SurveyDataSearch;
import com.adobe.aemf.facilities.survey.SurveyDTO;
import com.adobe.aemf.facilities.survey.Surveyor;

@Component
@Service(value = JSONBasedXSSReportGenerator.class)
public class JSONBasedXSSReportGenerator {

	@Reference
	Surveyor surveyor;

	@Reference
	PortalConfigComponent portalConfig;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private PortalDataAccessManager pFM;

	@Reference
	private SurveyDataSearch surveyDataSearch;

	XSSFWorkbook workbook;
	Logger logger = LoggerFactory.getLogger(JSONBasedXSSReportGenerator.class);

	public Report generateReport(List<ReportRequest> reportsRequired,SlingHttpServletRequest request)
			throws PortalException {
		workbook = new XSSFWorkbook();
		// Create blank workbook
		while (workbook.getNumberOfSheets() > 1) {
			logger.info("clearing worksheet ...");
			workbook.removeSheetAt(0);
		}

		try {
			for (Iterator iterator = reportsRequired.iterator(); iterator
					.hasNext();) {
				ReportRequest formSearchItem = (ReportRequest) iterator.next();
				XSSFSheet spreadsheet = null;
				String workbookName = null;
				Map outMap = null;

				SurveyDTO survey = (SurveyDTO) surveyor
						.getSurvey(formSearchItem.getSurveyId());
				workbookName = getWorkBookName(formSearchItem.getSurveyId(),
						reportsRequired);
				outMap = surveyDataSearch.getSurveyData(survey.getSurveyId(),
						request);
				logger.debug("Transformed JSON to Map : " + outMap.toString());

				int sheetIndex;
				if (workbookName != null) {
					// Create a blank sheet
					sheetIndex = workbook.getSheetIndex(workbookName);
					if (sheetIndex == -1) {
						spreadsheet = workbook.createSheet(workbookName);
					} else {
						spreadsheet = workbook.createSheet(workbookName
								+ RandomStringUtils.randomAlphabetic(1));
					}
				}

				if (formSearchItem.isRawData()) {
					createRawDataWorkSheet(spreadsheet, outMap);
				} else {
					Map statData = surveyDataSearch.getSurveyDataStatistics(survey.getSurveyId(), request);
					createStatisticsWorkSheet(spreadsheet, statData);
				}
			}
		} catch (PortalException e) {
			throw new PortalException(
					"Error while fetching Form Data for reports", e);
		} catch (RepositoryException e) {
			throw new PortalException(
					"Error while fetching Form Data for reports", e);
		} catch (LoginException e) {
			throw new PortalException(
					"Error while fetching Form Data for reports", e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("xlsx generation complete");
		return createReport();
	}

	void createRawDataWorkSheet(XSSFSheet spreadsheet, Map outMap) {
		XSSFRow row;
		XSSFRow headerRow;
		Map topUserMapData;
		Set userDataKeys = null;
		if (outMap != null && !outMap.isEmpty()) {
			int rowid = 1;
			headerRow = spreadsheet.createRow(0);

			// Lets store first user data properties then we'll iterate over the
			// user data for every property.
			Set<String> allUserDataKeys = outMap.keySet();
			if (outMap.get(allUserDataKeys.toArray()[0]) instanceof Map) {
				userDataKeys = ((HashMap) outMap
						.get(allUserDataKeys.toArray()[0])).keySet();
			}
			;

			logger.debug("User data properties : " + userDataKeys);
			logger.debug("All user data keys : " + allUserDataKeys);

			// Set values in header row cells
			int headerRowCellCount = 1;
			for (Iterator iterator2 = userDataKeys.iterator(); iterator2
					.hasNext();) {
				String property = (String) iterator2.next();
				Cell headerPropertyCell = headerRow.createCell(
						headerRowCellCount, Cell.CELL_TYPE_STRING);
				headerPropertyCell.setCellValue(property);
				headerRowCellCount++;
			}

			// Iterate each user data and fill user name and values in the row.
			for (String userDataKey : allUserDataKeys) {
				Map userDataMap = (Map) outMap.get(userDataKey);
				XSSFRow userDataRow = spreadsheet.createRow(rowid);
				Cell userNameCell = userDataRow.createCell(0,
						Cell.CELL_TYPE_STRING);
				userNameCell.setCellValue(userDataKey.substring(0,
						userDataKey.indexOf("_")));
				int userDataRowCellCount = 1;
				for (Iterator iterator2 = userDataKeys.iterator(); iterator2
						.hasNext();) {
					String userDataPropertyKey = (String) iterator2.next();
					Cell userDataRowCell = userDataRow.createCell(
							userDataRowCellCount, Cell.CELL_TYPE_STRING);
					userDataRowCell.setCellValue(userDataMap.get(
							userDataPropertyKey).toString());
					userDataRowCellCount++;
				}
				rowid++;
				logger.debug("Created row for userDataKey : " + userDataKey);
			}
		}
	}

	void createStatisticsWorkSheet(XSSFSheet spreadsheet, Map formData) { 
		XSSFRow row;
		if (formData != null && !formData.isEmpty()) {
			// Iterate over data and write to sheet
			Set<String> formProps = formData.keySet();
			int rowid = 0;
			// key is actually Question/text on a particular field in the form.
			for (String key : formProps) {
				int currentCellForKeyValue = 0;
				// Create a row with 100 cell width for Title/test of the form
				// field - in excel.
				row = spreadsheet.createRow(rowid++);
				Cell headerCell1 = row.createCell(0, Cell.CELL_TYPE_STRING);
				spreadsheet.addMergedRegion(new CellRangeAddress(rowid - 1,
						rowid - 1, 0, 100));
				headerCell1.setCellValue(key);

				XSSFRow rowforKey = spreadsheet.createRow(rowid++);
				XSSFRow rowforValue = spreadsheet.createRow(rowid++);
				Map<String, Integer> propValuesMap = (Map<String, Integer>) formData
						.get(key);
				logger.debug("Form property : " + key);
				for (Map.Entry<String, Integer> entry : propValuesMap
						.entrySet()) {
					logger.debug("	Prop name -> " + entry.getKey()
							+ " ::	Prop value -> " + entry.getValue());
					Cell cellValue = rowforKey
							.createCell(currentCellForKeyValue);
					cellValue.setCellValue((String) entry.getKey());
					Cell cellValueCount = rowforValue
							.createCell(currentCellForKeyValue);
					cellValueCount.setCellValue(entry.getValue());
					currentCellForKeyValue++;
				}

				// Create chart
				Drawing drawing = spreadsheet.createDrawingPatriarch();
				ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0,
						rowid + 1, 10, rowid + 15);

				Chart chart = drawing.createChart(anchor);
				ChartLegend legend = chart.getOrCreateLegend();
				legend.setPosition(LegendPosition.TOP_RIGHT);

				LineChartData data = chart.getChartDataFactory()
						.createLineChartData();

				// Use a category axis for the bottom axis.
				ChartAxis bottomAxis = chart.getChartAxisFactory()
						.createCategoryAxis(AxisPosition.TOP);
				ValueAxis leftAxis = chart.getChartAxisFactory()
						.createValueAxis(AxisPosition.RIGHT);
				leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

				ChartDataSource<Number> xs = DataSources.fromNumericCellRange(
						spreadsheet, new CellRangeAddress(
								rowforKey.getRowNum(), rowforKey.getRowNum(),
								0, currentCellForKeyValue - 1));
				ChartDataSource<Number> ys = DataSources.fromNumericCellRange(
						spreadsheet,
						new CellRangeAddress(rowforValue.getRowNum(),
								rowforValue.getRowNum(), 0,
								currentCellForKeyValue - 1));

				LineChartSeries chartSerie = data.addSeries(xs, ys);
				chartSerie.setTitle(key);
				chart.plot(data, bottomAxis, leftAxis);
				rowid = rowid + 20;
			}
		}
	}

	private String getWorkBookName(String surveyId,
			List<ReportRequest> reportsRequired) {
		for (ReportRequest reportRequest : reportsRequired) {
			if (reportRequest.getSurveyId().equalsIgnoreCase(surveyId)) {
				return reportRequest.getWorkBookName();
			}
		}
		return "WorkBook";
	}

	private Report createReport() {
		Report rpt = new XSSReport(workbook);
		rpt.setName("report.xlsx");
		return rpt;
	}

}
