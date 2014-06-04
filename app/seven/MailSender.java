package seven;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Created by evans on 5/29/14.
 * <p/>
 * This implementation only allow SEVEN's internal mail server.
 */
public class MailSender {

    public static void send(Map<String, String> mailConf, String recipientEmail, String ccEmail,
                            String title, String message) throws MessagingException {

        // System.setProperty("mail.debug", "true");

        final String username = mailConf.get("smtp.user");
        final String password = mailConf.get("smtp.password");
        final String host = mailConf.get("smtp.host");
        final String port = mailConf.get("smtp.port");

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", String.valueOf(port));
        props.setProperty("mail.smtp.auth", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(username + "@seven.com")); // fixme: here I just bind to internal mail server
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

        if (ccEmail.length() > 0) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
        }

        msg.setSubject(title);
        msg.setText(message, "utf-8", "html");
        msg.setSentDate(new Date());

        Transport.send(msg);
    }

}
