package com.play.util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Classe utility per l'invio di email.
 * Utilizza il protocollo SMTP di Gmail per l'invio delle email.
 * 
 * @author Play Team
 * @version 1.0
 */
public class EmailSender {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "testsender54367920@gmail.com";
    private static final String PASSWORD = "jijo aodi ogpf fsic";

    /**
     * Invia un'email utilizzando il servizio SMTP di Gmail.
     * 
     * @param to Indirizzo email del destinatario
     * @param subject Oggetto dell'email
     * @param body Corpo dell'email
     * @throws IllegalArgumentException se uno dei parametri è null o vuoto
     * @throws MessagingException se si verifica un errore durante l'invio dell'email
     */
    public static void sendEmail(String to, String subject, String body) throws MessagingException {
        validateParameters(to, subject, body);
        
        Properties props = configureSmtpProperties();
        Session session = createSmtpSession(props);
        Message message = createEmailMessage(session, to, subject, body);
        
        try {
            Transport.send(message);
            System.out.println("Email inviata con successo a " + to);
        } catch (MessagingException e) {
            System.err.println("Errore durante l'invio dell'email a " + to + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Valida i parametri di input per l'invio dell'email.
     * 
     * @param to Indirizzo email del destinatario
     * @param subject Oggetto dell'email
     * @param body Corpo dell'email
     * @throws IllegalArgumentException se uno dei parametri è null o vuoto
     */
    private static void validateParameters(String to, String subject, String body) {
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("L'indirizzo email del destinatario non può essere vuoto");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("L'oggetto dell'email non può essere vuoto");
        }
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Il corpo dell'email non può essere vuoto");
        }
        if (!to.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Formato indirizzo email non valido");
        }
    }

    /**
     * Configura le proprietà SMTP per l'invio dell'email.
     * 
     * @return Properties configurate per SMTP
     */
    private static Properties configureSmtpProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        return props;
    }

    /**
     * Crea una sessione SMTP autenticata.
     * 
     * @param props Proprietà SMTP
     * @return Sessione SMTP configurata
     */
    private static Session createSmtpSession(Properties props) {
        return Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }

    /**
     * Crea un messaggio email con i parametri specificati.
     * 
     * @param session Sessione SMTP
     * @param to Indirizzo email del destinatario
     * @param subject Oggetto dell'email
     * @param body Corpo dell'email
     * @return Messaggio email configurato
     * @throws MessagingException se si verifica un errore durante la creazione del messaggio
     */
    private static Message createEmailMessage(Session session, String to, String subject, String body) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);
        return message;
    }
}
