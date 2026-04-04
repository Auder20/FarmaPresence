package com.farmapresence.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 1. Enviar mensaje simple (texto plano)
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    // 2. Enviar mensaje con archivo adjunto
    public void sendMessageWithAttachment(String to, String subject, String text, File file) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        if (file != null && file.exists()) {
            FileSystemResource fileResource = new FileSystemResource(file);
            helper.addAttachment(file.getName(), fileResource);
        }

        mailSender.send(message);
    }

    // ✅ 3. Enviar mensaje HTML para recuperación de contraseña
    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = es HTML
        helper.setFrom("audergonzalez1@gmail.com"); // Reemplaza por tu correo configurado en properties

        mailSender.send(message);
    }
}
