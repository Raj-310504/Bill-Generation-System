package com.example.BillGeneration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    public void sendSms(String mobile, String msg) {
        smsService.sendSms(mobile, msg);
    }

    public void sendEmail(String to, String subject, String body) {
        emailService.sendMail(to, subject, body);
    }

    public void sendWhatsApp(String to, String msg) {
        smsService.sendWhatsApp(to, msg);
    }
}
