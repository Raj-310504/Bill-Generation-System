package com.example.BillGeneration.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "twilio")
public class TwilioProperties {

    private final Account account = new Account();
    private final Auth auth = new Auth();
    private final From from = new From();
    private final Whatsapp whatsapp = new Whatsapp();

    @Getter
    @Setter
    public static class Account {
        private String sid;
    }

    @Getter
    @Setter
    public static class Auth {
        private String token;
    }

    @Getter
    @Setter
    public static class From {
        private String number;
    }

    @Getter
    @Setter
    public static class Whatsapp {
        private String from;
        private String to = "";
    }
}
