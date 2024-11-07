package com.example.demo.service.admin;

import com.example.demo.entity.DataMailDTO;
import jakarta.mail.MessagingException;

public interface EmailService {

    void sendEmail(String to, String subject, String body);

    void sendHtmlMail(DataMailDTO dataMail, String templateName) throws MessagingException;

}
