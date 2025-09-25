package com.github.treladev.exception;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
