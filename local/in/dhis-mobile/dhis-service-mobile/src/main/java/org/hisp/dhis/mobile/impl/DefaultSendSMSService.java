package org.hisp.dhis.mobile.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.mobile.api.RawSMS;
import org.hisp.dhis.mobile.api.SendSMS;
import org.hisp.dhis.mobile.api.SendSMSService;
import org.hisp.dhis.mobile.api.SendSMSStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultSendSMSService implements SendSMSService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SendSMSStore sendSMSStore;

    public void setSendSMSStore( SendSMSStore sendSMSStore )
    {
        this.sendSMSStore = sendSMSStore;
    }
    
    // -------------------------------------------------------------------------
    // SendSMS
    // -------------------------------------------------------------------------
    
    public void addSendSMS( SendSMS sendSMS )
    {
        sendSMSStore.addSendSMS( sendSMS );
    }
    
    public void updateSendSMS( SendSMS sendSMS )
    {
        sendSMSStore.updateSendSMS( sendSMS );
    }
    
    public void deleteSendSMS( SendSMS sendSMS )
    {
        sendSMSStore.deleteSendSMS( sendSMS );
    }
    
    public Collection<SendSMS> getSendSMS( int start, int end )
    {
        return sendSMSStore.getSendSMS( start, end );
    }

    public Collection<SendSMS> getAllSendSMS( )
    {
        return sendSMSStore.getAllSendSMS();
    }
    
    public long getRowCount()
    {
        return sendSMSStore.getRowCount();
    }
    
    public SendSMS getSendSMS( String senderInfo )
    {
        return sendSMSStore.getSendSMS( senderInfo );
    }
    
    // -------------------------------------------------------------------------
    // RawSMS
    // -------------------------------------------------------------------------

    public void addRawSMS( RawSMS rawSMS )
    {
        sendSMSStore.addRawSMS( rawSMS );
    }
    
    public void updateRawSMS( RawSMS rawSMS )
    {
        sendSMSStore.updateRawSMS( rawSMS );
    }
    
    public void deleteRawSMS( RawSMS rawSMS )
    {
        sendSMSStore.deleteRawSMS( rawSMS );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<RawSMS> getRawSMS( int start, int end )
    {
        return sendSMSStore.getRawSMS( start, end );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<RawSMS> getAllRawSMS( )
    {
        return sendSMSStore.getAllRawSMS();
    }
    
    public long getRawSMSRowCount()
    {
        return sendSMSStore.getRawSMSRowCount();
    }
    
    public RawSMS getRawSMS( String senderInfo )
    {
        return sendSMSStore.getRawSMS( senderInfo );
    }

}
