package com.example.landahand.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRequestEmail(String toEmail, String requesterName, String requesterEmail, String postTitle, String phoneNumber,  String userBiography) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Имате ново барање на вашиот оглас");
        message.setText("Драг корисник,\n\nИмате добиено ново барање на вашиот оглас: " + postTitle +
                "\nИме на праќачот:" + requesterName +
                "\nЕ-пошта: " + requesterEmail +
                "\nБрој: " + phoneNumber +
                "\nКратка биографија на праќачот: " + userBiography +
                "\n\nЛогирајте се на вашиот кориснички профил за да ја одобрите или одбиете понудата.");

        mailSender.send(message);
    }
}
