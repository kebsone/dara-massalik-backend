package com.daramassalik.backend.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
@Component
@ConditionalOnProperty(
        name = "app.sms.provider",
        havingValue = "logging",
        matchIfMissing = true
)
public class LoggingSmsSender implements SmsSender {
    private static final Logger log = LoggerFactory.getLogger(LoggingSmsSender.class);

    @Override
    public void sendOtp(String phoneNumber, String code) {
        log.info("DEV SMS OTP for {}: {}", phoneNumber, code);
    }
}
