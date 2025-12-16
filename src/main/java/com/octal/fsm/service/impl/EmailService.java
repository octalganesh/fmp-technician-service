package com.octal.fsm.service.impl;

import com.google.gson.Gson;
import com.octal.fsm.clients.NotificationClient;
import com.octal.fsm.dto.EmailDTO;
import com.octal.fsm.dto.EmailRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private NotificationClient notificationClient;

    public void sendMail(EmailDTO mail) {
        try{
            EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
            emailRequestDTO.setToMail(mail.getMailTo());
            emailRequestDTO.setSubject(mail.getSubject());
            // Load Thymeleaf template
            Context context = new Context();
            context.setVariables(mail.getProps());

            String htmlContent = templateEngine.process(mail.getTemplateName(), context);
            emailRequestDTO.setBody(htmlContent);

            if (mail.getAttachments() != null) {
                List<String> attachments = mail.getAttachments()
                        .stream().map(String::valueOf)  // converts Object → String safely
                        .collect(Collectors.toList());
                emailRequestDTO.setAttachments(attachments);
            }
            notificationClient.sendEmailToUser(emailRequestDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

