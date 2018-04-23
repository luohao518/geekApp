package xyz.geekweb.paypal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {

    @Value("${paypal.client.app}")
    public String clientId;

    @Value("${paypal.client.secret}")
    public String clientSecret;

    @Value("${paypal.mode}")
    public String mode;

}