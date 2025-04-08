package com.play.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    public static void sendEmail(String to, String subject, String body) {
        final String username = "testsender54367920@gmail.com";
        final String password = "jijo aodi ogpf fsic";
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");  // Abilita STARTTLS
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); // Mittente
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("Email inviata a " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Errore durante l'invio dell'email a " + to);
        }
    }
}
