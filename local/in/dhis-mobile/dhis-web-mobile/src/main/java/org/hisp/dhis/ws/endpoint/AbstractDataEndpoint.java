package org.hisp.dhis.ws.endpoint;

import org.hisp.dhis.ws.service.DataService;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

/**
 * @author THAI
 *
 */
public abstract class AbstractDataEndpoint extends AbstractMarshallingPayloadEndpoint{
	
	/**
	 * initialed in spring-ws-servlet.xml
	 */
	protected DataService dataService;

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint#invokeInternal(java.lang.Object)
	 */
	protected abstract Object invokeInternal(Object request) throws Exception;
}
