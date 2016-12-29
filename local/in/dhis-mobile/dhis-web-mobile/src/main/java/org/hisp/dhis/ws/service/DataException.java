package org.hisp.dhis.ws.service;

/**
 * @author THAI
 * Soap fault response
 */
public class DataException extends Exception {
	public DataException(String message) {
		    super(message);
	}
}
