package com.daramassalik.backend.auth;

public class OtpInvalidException extends RuntimeException {
    public OtpInvalidException(String message) { super(message); }
}
