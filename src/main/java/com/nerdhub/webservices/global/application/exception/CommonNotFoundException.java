package com.nerdhub.webservices.global.application.exception;

public class CommonNotFoundException extends CommonUseCaseException {
    public CommonNotFoundException(String message, Object details) {
        super(message, details);
    }

    public CommonNotFoundException(String message) {
        super(message);
    }
}
