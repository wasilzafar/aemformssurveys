/**
 * 
 */
package com.adobe.aemf.facilities.core;

import java.util.List;
import java.util.Map;

import com.adobe.aemf.facilities.exceptions.PortalException;
import com.adobe.aemf.facilities.search.SearchSpec;

/**
 * This interface defines the API to access all the data submitted to portal. 
 * It can be implemented by any class which uses a particular data persistence mechanism e.g. RDBMS, CRX etc.  
 * @author zafar
 *
 */
public interface PortalDataAccessManager {
	/**
	 * Returns a list of all the form available.
	 * @return
	 */
	Map<String, String> getAllForms() throws PortalException;
	
	List getFormMetadata(String formName) throws PortalException;
	
	List getGeos() throws PortalException;
	
	String getFormPath(String formName);
	
}
