package com.adobe.aemf.facilities.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.jackrabbit.util.Base64;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PortalUtils {

	public static Document getDocumentObject(InputStream input) {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc = null;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(input);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	
	public static Date parseDateWithDefault(String dateParam, Date currentDate) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	Date date = null;
	try {
		date = df.parse(dateParam);
	} catch (ParseException e1) {
		System.out.println("Unable to parse date(required format - yyyy-MM-dd ) found : date parameter -> "+ dateParam);
	}
		
		return date;
	}

	public static Date getCurrentDate(){
		return new Date();
	}
	
	public static String parseString(Object param){
		return ObjectUtils.toString(param);
	}
	
	public static String parseStringWithDefault(Object param, String defaultValue){
		return ObjectUtils.toString(param,defaultValue);
	}
	
	public static String getDayOfWeek(int value) {
		String day = "";
		switch (value) {
		case Calendar.SUNDAY:
			day = "Sunday";
			break;
		case Calendar.MONDAY:
			day = "Monday";
			break;
		case Calendar.TUESDAY:
			day = "Tuesday";
			break;
		case Calendar.WEDNESDAY:
			day = "Wednesday";
			break;
		case Calendar.THURSDAY:
			day = "Thursday";
			break;
		case Calendar.FRIDAY:
			day = "Friday";
			break;
		case Calendar.SATURDAY:
			day = "Saturday";
			break;
		}
		return day;

	}
	
	public static Date getPreviousNthDate(int n){
	     return new Date(System.currentTimeMillis()-n*7*24*60*60*1000);
	}
	
	public static int ordinalIndexOf(String str, String s, int n) {
		int pos = str.indexOf(s, 0);
		while (n-- > 0 && pos != -1)
			pos = str.indexOf(s, pos + 1);
		return pos;
	}
	
	public static String encryptInt(String str) {
		long numFromStr = Long.parseLong(str);
		String hexStr = Long.toHexString(numFromStr);
		return hexStr;
		}
	
	public static String decryptInt(String str) {
		NumberFormat f = NumberFormat.getInstance();
		f.setGroupingUsed(false);
		long dbl = Long.parseLong(str, 16);
		return f.format(dbl);
	}
}
