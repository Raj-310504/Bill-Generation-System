package com.example.BillGeneration.service;

import com.example.BillGeneration.config.properties.TwilioProperties;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private final TwilioProperties twilioProperties;

    public SmsService(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }

    public void sendSms(String mobile, String msg) {
        Message.creator(
                new PhoneNumber(mobile),
                new PhoneNumber(twilioProperties.getFrom().getNumber()),
                msg
        ).create();
    }

    public void sendWhatsApp(String to, String msg) {
        String resolvedTo = resolveWhatsAppTo(to);
        Message.creator(
                new PhoneNumber(resolvedTo),
                new PhoneNumber(twilioProperties.getWhatsapp().getFrom()),
                msg
        ).create();
    }

    private String resolveWhatsAppTo(String to) {
        String whatsappToDefault = twilioProperties.getWhatsapp().getTo();
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
