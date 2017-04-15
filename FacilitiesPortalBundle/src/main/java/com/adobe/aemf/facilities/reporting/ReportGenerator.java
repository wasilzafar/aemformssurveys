package com.adobe.aemf.facilities.reporting;

import java.util.List;

import com.adobe.aemf.facilities.exceptions.PortalException;
import com.adobe.aemf.facilities.um.UserIdentity;


/**
 * This interface is abstract creator, implemented by a class which generates different types of report(product) e.g. Excel, CSV, PDF etc.
 * @author zafar
 *
 */
public interface ReportGenerator {

	public abstract Report generateReport(List<ReportRequest> reportsRequired, UserIdentity uId) throws PortalException;

}
