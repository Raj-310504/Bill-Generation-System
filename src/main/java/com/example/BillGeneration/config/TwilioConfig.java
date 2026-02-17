package com.example.BillGeneration.config;

import com.example.BillGeneration.config.properties.TwilioProperties;
import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    private final TwilioProperties twilioProperties;

    public TwilioConfig(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }

    @PostConstruct
    public void init() {
        Twilio.init(
                twilioProperties.getAccount().getSid(),
                twilioProperties.getAuth().getToken()
        );
        System.out.println("Twilio Initialized");
    }
}

