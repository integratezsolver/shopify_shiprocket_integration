package com.integratez.platform.modules.auth.service;

import lombok.NoArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Verify your email address";
        String verificationUrl = "http://localhost:8080/auth/verify?token=" + token;

        String body = """
                Welcome to Integratez Platform!

                Please click the link below to verify your email:
                %s

                This link will expire in 15 minutes.

                Regards,
                Integratez Team
                """.formatted(verificationUrl);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}