package org.hisp.dhis.message;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Lars Helge Overland
 */
@Transactional
public class DefaultMessageService
    implements MessageService
{
    private static final Log log = LogFactory.getLog( DefaultMessageService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MessageConversationStore messageConversationStore;

    public void setMessageConversationStore( MessageConversationStore messageConversationStore )
    {
        this.messageConversationStore = messageConversationStore;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private List<MessageSender> messageSenders;

    @Autowired
    public void setMessageSenders( List<MessageSender> messageSenders )
    {
        this.messageSenders = messageSenders;

        log.info( "Found the following message senders: " + messageSenders );
    }

    // -------------------------------------------------------------------------
    // MessageService implementation
    // -------------------------------------------------------------------------

    public int sendMessage( String subject, String text, String metaData, Set<User> users )
    {
        // ---------------------------------------------------------------------
        // Add feedback recipients to users if they are not there
        // ---------------------------------------------------------------------

        UserGroup userGroup = configurationService.getConfiguration().getFeedbackRecipients();

        if ( userGroup != null && userGroup.getMembers().size() > 0 )
        {
            users.addAll( userGroup.getMembers() );
        }

        User sender = currentUserService.getCurrentUser();

        if ( sender != null )
        {
            users.add( sender );
        }

        // ---------------------------------------------------------------------
        // Instantiate message, content and user messages
        // ---------------------------------------------------------------------

        MessageConversation conversation = new MessageConversation( subject, sender );

        conversation.addMessage( new Message( text, metaData, sender ) );

        for ( User user : users )
        {
            boolean read = user != null && user.equals( sender );

            conversation.addUserMessage( new UserMessage( user, read ) );
        }

        int id = saveMessageConversation( conversation );

        invokeMessageSenders( subject, text, sender, users );

        return id;
    }

    public int sendFeedback( String subject, String text, String metaData )
    {
        return sendMessage( subject, text, metaData, new HashSet<User>() );
    }

    public void sendReply( MessageConversation conversation, String text, String metaData )
    {
        User sender = currentUserService.getCurrentUser();

        Message message = new Message( text, metaData, sender );

        conversation.markReplied( sender, message );

        updateMessageConversation( conversation );

        invokeMessageSenders( conversation.getSubject(), text, sender, conversation.getUsers() );
    }

    public int sendCompletenessMessage( CompleteDataSetRegistration registration )
    {
        UserGroup userGroup = configurationService.getConfiguration().getCompletenessRecipients();

        if ( userGroup != null && userGroup.getMembers().size() > 0 )
        {
            User sender = currentUserService.getCurrentUser();

            //TODO i18n and string externalization            
            String subject = "Form registered as complete";
            String text = "The form " + registration.getDataSet() + " was registered as complete for period " +
                registration.getPeriod().getName() + " and organisation unit " + registration.getSource();

            MessageConversation conversation = new MessageConversation( subject, sender );

            conversation.addMessage( new Message( text, null, sender ) );

            for ( User user : userGroup.getMembers() )
            {
                conversation.addUserMessage( new UserMessage( user ) );
            }

            int id = saveMessageConversation( conversation );

            invokeMessageSenders( subject, text, sender, userGroup.getMembers() );

            return id;
        }

        return 0;
    }

    public int saveMessageConversation( MessageConversation conversation )
    {
        return messageConversationStore.save( conversation );
    }

    public void updateMessageConversation( MessageConversation conversation )
    {
        messageConversationStore.update( conversation );
    }

    public MessageConversation getMessageConversation( int id )
    {
        return messageConversationStore.get( id );
    }

    public MessageConversation getMessageConversation( String uid )
    {
        return messageConversationStore.getByUid( uid );
    }

    public long getUnreadMessageConversationCount()
    {
        return messageConversationStore.getUnreadUserMessageConversationCount( currentUserService.getCurrentUser() );
    }

    public long getUnreadMessageConversationCount( User user )
    {
        return messageConversationStore.getUnreadUserMessageConversationCount( user );
    }

    public List<MessageConversation> getMessageConversations( int first, int max )
    {
        return messageConversationStore.getMessageConversations( currentUserService.getCurrentUser(), first, max );
    }
    
    public int getMessageConversationCount()
    {
        return messageConversationStore.getMessageConversationCount( currentUserService.getCurrentUser() );
    }
    
    public List<MessageConversation> getAllMessageConversations()
    {
        return messageConversationStore.getMessageConversations( null, null, null );
    }

    public void deleteMessages( User user )
    {
        messageConversationStore.deleteMessages( user );
        messageConversationStore.deleteUserMessages( user );
        messageConversationStore.removeUserFromMessageConversations( user );
    }
        
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void invokeMessageSenders( String subject, String text, User sender, Set<User> users )
    {
        for ( MessageSender messageSender : messageSenders )
        {
            messageSender.sendMessage( subject, text, sender, users );
        }
    }
}
