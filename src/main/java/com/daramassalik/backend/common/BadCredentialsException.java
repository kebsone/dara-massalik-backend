package com.daramassalik.backend.common;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException() {
        super("Invalid email or password");
    }
}
