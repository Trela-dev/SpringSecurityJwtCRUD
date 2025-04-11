package com.github.treladev.exception;

public class UsernameAlreadyInUseException extends RuntimeException {
    public UsernameAlreadyInUseException(String message){
        super(message);
    }
}
