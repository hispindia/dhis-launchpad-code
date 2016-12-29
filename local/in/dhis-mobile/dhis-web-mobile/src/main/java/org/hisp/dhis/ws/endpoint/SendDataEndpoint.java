package org.hisp.dhis.ws.endpoint;

import org.hisp.dhis.ws.DataSMS;
import org.hisp.dhis.ws.SendDataRequest;
import org.hisp.dhis.ws.SendDataResponse;

/**
 * @author THAI
 *
 */
public class SendDataEndpoint extends AbstractDataEndpoint {

	@Override
	protected Object invokeInternal(Object request) throws Exception {
		// TODO Auto-generated method stub
		
		// Get data from request
	    SendDataRequest sendDataRequest = (SendDataRequest) request;
	    
	    // Create response Object
	    SendDataResponse sendDataResponse = new SendDataResponse();
	    
	    // Process the request in the system and set the value for response Data
	    // We can do everything in sendData() function
	    DataSMS myReturnData = dataService.sendData(sendDataRequest.getDataSMS());
	    
	    // Set value for response Object
	    sendDataResponse.setDataSMS(myReturnData);
	    // response
		return sendDataResponse;
	}

}
