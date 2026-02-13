package com.banka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Value("${spring.mail.username:noreply@banka.com}")
    private String fromEmail;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    public boolean sendEmail(String to, String subject, String body) {
        if (mailSender == null) {
            logger.warn("JavaMailSender yapılandırılmamış! Email gönderilemedi: {} -> {}", fromEmail, to);
            logger.warn("Email içeriği: Subject: {}, Body: {}", subject, body);
            return false;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            logger.info("Email başarıyla gönderildi: {} -> {}", fromEmail, to);
            return true;
        } catch (Exception e) {
            logger.error("Email gönderme hatası: {} -> {} - Hata: {}", to, e.getMessage(), e.getClass().getSimpleName());
            return false;
        }
    }
}
