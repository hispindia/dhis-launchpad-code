package org.hisp.dhis.ws.service;

import org.hisp.dhis.ws.DataSMS;

/**
 * @author THAI
 * Data Service Interface
 */
public interface DataService {
	DataSMS sendData (DataSMS data) throws DataException;
}
