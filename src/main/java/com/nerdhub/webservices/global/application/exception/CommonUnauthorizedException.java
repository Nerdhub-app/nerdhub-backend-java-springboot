package com.nerdhub.webservices.global.application.exception;

public class CommonUnauthorizedException extends CommonUseCaseException {
    public CommonUnauthorizedException(String message, Object details) {
        super(message, details);
    }

    public CommonUnauthorizedException(String message) {
        super(message);
    }
}
