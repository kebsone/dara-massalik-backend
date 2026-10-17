package com.daramassalik.backend.common;

import com.daramassalik.backend.auth.InvalidRefreshTokenException;
import com.daramassalik.backend.auth.OtpInvalidException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleUnauthorized(BadCredentialsException ex, HttpServletRequest request) {
        return response(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(OtpInvalidException.class)
    public ResponseEntity<ApiError> handleOtp(OtpInvalidException ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleRefresh(InvalidRefreshTokenException ex, HttpServletRequest request) {
        return response(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(SmsDeliveryException.class)
    public ResponseEntity<ApiError> handleSms(SmsDeliveryException ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_GATEWAY, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));
        return response(HttpStatus.BAD_REQUEST, message, request);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(
                new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI())
        );
    }
}
