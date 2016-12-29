package org.hisp.dhis.userstatus.util;

/**
 * Gaurav<gaurav08021@gmail.com>, 8/23/12 [10:37 AM]
 **/

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendDataStatusEmail {


    public void sendReport(final String userID, final String password, final String mailServer, final String receiver, String messageContent, final String subject) {

        Properties props = new Properties();

        String msgFooter = "<br/><br/><h3>Thank You<h3><h2>DMS DATA STATUS SERVICE<h2>";

        String disclaimer = "<br/><br/><br/>*This is a system generated message. For any queries or complaints/bug-reports please reach us at [gaurav08021@gmail.com]"
                +"</body>"
                +"</html>";

        //----------------------------------SET MAIL PROPERTIES-------------------------------------//

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",mailServer);
        props.put("mail.smtp.port", "587");


        //---------------------------------INITIATE MAIL SESSION-----------------------------------//

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userID, password);
                    }
                });

        try
        {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress("noreply-hispdatabank@gmail.com"));

            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(receiver));

            message.setSubject(subject);

            messageContent = messageContent + msgFooter + disclaimer;

            message.setContent(messageContent, "text/html");

            Transport.send(message);
        }

        catch (MessagingException e)
        {
            throw new RuntimeException(e);
        }
    }
}