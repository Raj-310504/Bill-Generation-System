package com.example.BillGeneration.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.from.number}")
    private String fromNumber;

    @Value("${twilio.whatsapp.from}")
    private String whatsappFrom;

    @Value("${twilio.whatsapp.to:}")
    private String whatsappToDefault;

    public void sendSms(String mobile, String msg) {
        Message.creator(
                new PhoneNumber(mobile),
                new PhoneNumber(fromNumber),
                msg
        ).create();
    }

    public void sendWhatsApp(String to, String msg) {
        String resolvedTo = resolveWhatsAppTo(to);
        Message.creator(
                new PhoneNumber(resolvedTo),
                new PhoneNumber(whatsappFrom),
                msg
        ).create();
    }

    private String resolveWhatsAppTo(String to) {
        String target = whatsappToDefault == null || whatsappToDefault.isBlank()
                ? to
                : whatsappToDefault;
        if (target == null || target.isBlank()) {
            throw new RuntimeException("WhatsApp 'to' number is required");
        }
        if (target.startsWith("whatsapp:")) {
            return target;
        }
        return "whatsapp:" + target;
    }
}
