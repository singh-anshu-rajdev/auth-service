package com.smartBankElite.authserver.Utils;

import com.smartBankElite.authserver.DTO.RegisterUserDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendUserCreationEmail(RegisterUserDto input) {

        try {

            // Create thymeleaf context
            Context context = new Context();
            context.setVariable("name", input.getFullName());
            context.setVariable("username", input.getUserName());
            context.setVariable("password", input.getPassword());

            // Load and process template
            String htmlContent = templateEngine.process("user-created-email", context);

            // Create mail message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("smartbankelite@gmail.com", "SmartBank Elite");
            helper.setTo(input.getEmail());
            helper.setSubject("Welcome to SmartBank Elite – Account Created");
            helper.setText(htmlContent, true);

            // Send mail
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to set email encoding", e);
        }
    }
}
