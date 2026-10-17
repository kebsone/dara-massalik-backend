package com.daramassalik.backend.auth;

public interface SmsSender {
    void sendOtp(String phoneNumber, String code);
}
