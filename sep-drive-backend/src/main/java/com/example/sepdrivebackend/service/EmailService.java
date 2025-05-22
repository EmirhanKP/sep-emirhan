package com.example.sepdrivebackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


//"Wrapper" Klasse für Inhalt, Fehlerbehandlung + Debugging
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    //spring-boot-starter-mail Bean, SMTP per application.properties
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) { this.mailSender = mailSender; }

    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            //Debugging Hilfe
            logger.info("Sende Email an {} mit Betreff: {}", to, subject);
            logger.debug ("E-Mail-Inhalt: {}", text);

            mailSender.send(message);

            logger.info("E-mail versendet.");
        } catch (Exception e) {
            logger.error("Fehler beim Versenden der Email: {}", e.getMessage(), e);
            throw new RuntimeException("E-Mail konnte nicht gesendet werden.", e);
        }
    }
    
}
