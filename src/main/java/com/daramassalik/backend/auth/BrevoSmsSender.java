package com.daramassalik.backend.auth;

import com.daramassalik.backend.common.SmsDeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Real SMS sender backed by Brevo transactional SMS API.
 * Enabled only when app.sms.provider=brevo.
 */
@Component
@ConditionalOnProperty(name = "app.sms.provider", havingValue = "brevo")
public class BrevoSmsSender implements SmsSender {

    private static final String BREVO_SMS_URL = "https://api.brevo.com/v3/transactionalSMS/send";

    private final RestClient restClient;
    private final String apiKey;
    private final String sender;
    private static final Logger log =
            LoggerFactory.getLogger(BrevoSmsSender.class);
    public BrevoSmsSender(
            RestClient.Builder restClientBuilder,
            @Value("${app.sms.brevo.api-key:}") String apiKey,
            @Value("${app.sms.brevo.sender:DaraMassalik}") String sender) {
        this.restClient = restClientBuilder.build();
        this.apiKey = apiKey;
        this.sender = sender;
    }

    @Override
    public void sendOtp(String phoneNumber, String code) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new SmsDeliveryException("SMS provider is configured as Brevo but BREVO_API_KEY is missing.");
        }

        String recipient = phoneNumber.startsWith("+")
                ? phoneNumber.substring(1)
                : phoneNumber;

        String content = "Votre code Dara Massalik est : " + code + ". Il expire dans 5 minutes.";

        try {
            ResponseEntity<Map> response =     restClient.post()
                    .uri(BREVO_SMS_URL)
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "sender", sender,
                            "recipient", recipient,
                            "content", content,
                            "type", "transactional",
                            "tag", "daraMassalikOtp"
                    ))
                    .retrieve()
                    .toEntity(Map.class);
            log.info("Brevo SMS status: {}", response.getStatusCode());
            log.info("Brevo SMS response: {}", response.getBody());
        } catch (Exception ex) {
            throw new SmsDeliveryException("Impossible d'envoyer le code SMS. Veuillez réessayer.", ex);
        }
    }
}
