package org.hisp.dhis.mobile.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.mobile.api.RawSMS;
import org.hisp.dhis.mobile.api.RawSMSService;
import org.hisp.dhis.mobile.api.RawSMSStore;
import org.hisp.dhis.mobile.api.SendSMS;
import org.hisp.dhis.mobile.api.XmlCreatorService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultRawSMSService implements RawSMSService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private RawSMSStore rawSMSStore;

    public void setRawSMSStore( RawSMSStore rawSMSStore )
    {
        this.rawSMSStore = rawSMSStore;
    }

    XmlCreatorService xmlCreatorService;

    public void setXmlCreatorService( XmlCreatorService xmlCreatorService )
    {
        this.xmlCreatorService = xmlCreatorService;
    }

    // -------------------------------------------------------------------------
    // RawSMS
    // -------------------------------------------------------------------------

    @Override
    public void addRawSMS( RawSMS rawSMS )
    {
        rawSMSStore.addRawSMS( rawSMS );
    }

    @Override
    public void deleteRawSMS( RawSMS rawSMS )
    {
        rawSMSStore.deleteRawSMS( rawSMS );
    }

    @Override
    public Collection<RawSMS> getAllRawSMS()
    {
        return rawSMSStore.getAllRawSMS();
    }

    @Override
    public Collection<RawSMS> getRawSMS( int start, int end )
    {
        return rawSMSStore.getRawSMS( start, end );
    }

    @Override
    public RawSMS getRawSMS( String senderInfo )
    {
        return rawSMSStore.getRawSMS( senderInfo );
    }

    @Override
    public long getRowCount()
    {
        return rawSMSStore.getRowCount();
    }

    @Override
    public void updateRawSMS( RawSMS rawSMS )
    {
        rawSMSStore.updateRawSMS( rawSMS );
    }
    
    /*
    public String getRawSMS_CreateXML()
    {
        int successCount = 0;
        int failCount = 0;

        int inboundCount = (int) getRowCount();

        List<RawSMS> rawSMSList = new ArrayList<RawSMS>();

        if ( inboundCount == 0 )
        {
            return "No RawSMS to process";
        } 
        else
        {
            if ( inboundCount < RawSMS.sendSMSRange )
            {
                rawSMSList.addAll( getRawSMS( 0, inboundCount ) );
            } 
            else
            {
                rawSMSList.addAll( getRawSMS( 0, RawSMS.sendSMSRange - 1 ) );
            }
        }

        for ( RawSMS rawSMS : rawSMSList )
        {
            try
            {
                String mobileNumber = rawSMS.getSenderInfo().split( "_" )[0];
                String timeStamp = rawSMS.getSenderInfo().split( "_" )[1];
                createXMLFile( mobileNumber, timeStamp, rawSMS.getMessageContent() );
                deleteRawSMS( rawSMS );
                successCount++;
            }
            catch( Exception e )
            {
                e.printStackTrace();
                failCount++;
            }
        }

        return "RawSMS Successfully Processed : " + successCount + " Failed : " + failCount;
    }
    */

    /*
    public void createXMLFile( String mobileNumber, String timeStamp, String data )
    {
        try
        {
            xmlCreatorService.setPhoneNumber( mobileNumber );
            //SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" );
            //String timeStamp = dateFormat.format( sendTime );
            xmlCreatorService.setSendTime( timeStamp );
            xmlCreatorService.setInfo( data );
            xmlCreatorService.run(); //should be made thread-safe
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
    */

}
