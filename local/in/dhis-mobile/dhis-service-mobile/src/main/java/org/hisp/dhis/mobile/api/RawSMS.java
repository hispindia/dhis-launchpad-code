package org.hisp.dhis.mobile.api;

import java.io.Serializable;

public class RawSMS implements Serializable
{

    public static final int sendSMSRange = 30;
    
    /**
     * Sender Phone Number with time of Received, Unique and Required.
     */
    private String senderInfo;

    /**
     * Message to send to sender, Required
     */
    private String messageContent ;

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public RawSMS()
    {
        
    }
    
    public RawSMS( String senderInfo, String messageContent )
    {
        this.senderInfo = senderInfo;
        this.messageContent = messageContent;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return senderInfo.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof RawSMS) )
        {
            return false;
        }

        final RawSMS other = (RawSMS) o;

        return senderInfo.equals( other.getSenderInfo() );
    }

    // -------------------------------------------------------------------------
    // Setter & Getter
    // -------------------------------------------------------------------------

    public String getSenderInfo()
    {
        return senderInfo;
    }

    public void setSenderInfo( String senderInfo )
    {
        this.senderInfo = senderInfo;
    }

    public String getMessageContent()
    {
        return messageContent;
    }

    public void setMessageContent( String messageContent )
    {
        this.messageContent = messageContent;
    }
        
}
