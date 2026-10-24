package com.daramassalik.backend.auth;

import com.daramassalik.backend.common.SmsDeliveryException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Fails closed when OTP SMS has not yet been configured for this deployment. */
@Component
@ConditionalOnProperty(name = "app.sms.provider", havingValue = "disabled")
public class DisabledSmsSender implements SmsSender {
    @Override
    public void sendOtp(String phoneNumber, String code) {
        throw new SmsDeliveryException(
                "La connexion par SMS n'est pas encore configurée. Veuillez utiliser la connexion administrateur ou contacter le responsable."
        );
    }
}
