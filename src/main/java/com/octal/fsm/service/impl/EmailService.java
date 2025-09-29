package com.octal.fsm.service.impl;

import com.octal.fsm.dto.EmailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendMail(EmailDTO mail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(mail.getMailTo());
        helper.setSubject(mail.getSubject());

        // Load Thymeleaf template
        Context context = new Context();
        context.setVariables(mail.getProps());

        String htmlContent = templateEngine.process(mail.getTemplateName(), context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}

