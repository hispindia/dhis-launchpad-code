package org.hisp.dhis.message.hibernate;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Query;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageConversationStore;
import org.hisp.dhis.user.User;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Lars Helge Overland
 */
public class HibernateMessageConversationStore
    extends HibernateIdentifiableObjectStore<MessageConversation> implements MessageConversationStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }
    
    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public List<MessageConversation> getMessageConversations( User user, Integer first, Integer max )
    {
        String sql = 
            "select mc.messageconversationid, mc.uid, mc.subject, mc.lastmessage, ui.surname, ui.firstname, ( " +
                "select isread from usermessage " +
                "where usermessage.usermessageid=mu.usermessageid " +
                "and mu.messageconversationid=mc.messageconversationid ) as isread, ( " +
                "select isfollowup from usermessage " + 
                "where usermessage.usermessageid=mu.usermessageid " +
                "and mu.messageconversationid=mc.messageconversationid ) as isfollowup " +
            "from messageconversation mc " +
            "left join messageconversation_usermessages mu on mc.messageconversationid=mu.messageconversationid " +
            "left join usermessage um on mu.usermessageid=um.usermessageid " +
            "left join userinfo ui on mc.lastsenderid=ui.userinfoid ";
        
        if ( user != null )
        {
            sql += "where um.userid=" + user.getId() + " ";
        }
        
        sql += "order by mc.lastmessage desc ";
        
        if ( first != null && max != null )
        {
            sql += statementBuilder.limitRecord( first, max );
        }
        
        final List<MessageConversation> conversations = jdbcTemplate.query( sql, new RowMapper<MessageConversation>()
        {
            public MessageConversation mapRow( ResultSet resultSet, int count ) throws SQLException
            {
                MessageConversation conversation = new MessageConversation();
                
                conversation.setId( resultSet.getInt( 1 ) );
                conversation.setUid( resultSet.getString( 2 ) );
                conversation.setSubject( resultSet.getString( 3 ) );
                conversation.setLastMessage( resultSet.getDate( 4 ) );
                conversation.setLastSenderSurname( resultSet.getString( 5 ) );
                conversation.setLastSenderFirstname( resultSet.getString( 6 ) );                
                conversation.setRead( resultSet.getBoolean( 7 ) );
                conversation.setFollowUp( resultSet.getBoolean( 8 ) );
                
                return conversation;
            }            
        } );
        
        return conversations;
    }

    public int getMessageConversationCount( User user )
    {
        String sql = 
            "select count(*) from messageconversation mc " +
            "left join messageconversation_usermessages mu on mc.messageconversationid=mu.messageconversationid " +
            "left join usermessage um on mu.usermessageid=um.usermessageid " +
            "where um.userid=" + user.getId();

        return jdbcTemplate.queryForInt( sql );
    }
    
    public long getUnreadUserMessageConversationCount( User user )
    {
        String hql = "select count(*) from MessageConversation m join m.userMessages u where u.user = :user and u.read = false";
        
        Query query = getQuery( hql );
        query.setEntity( "user", user );
        query.setCacheable( true );
        
        return (Long) query.uniqueResult();
    }
    
    public int deleteMessages( User sender )
    {
        String hql = "delete Message m where m.sender = :sender";

        Query query = getQuery( hql );
        query.setEntity( "sender", sender );
        return query.executeUpdate();
    }
    
    public int deleteUserMessages( User user )
    {
        String hql = "delete UserMessage u where u.user = :user";

        Query query = getQuery( hql );
        query.setEntity( "user", user );
        return query.executeUpdate();
    }
    
    public int removeUserFromMessageConversations( User lastSender )
    {
        String hql = "update MessageConversation m set m.lastSender = null where m.lastSender = :lastSender";

        Query query = getQuery( hql );
        query.setEntity( "lastSender", lastSender );
        return query.executeUpdate();
    }
}