package com.nerdhub.webservices.global.application.exception;

public class CommonBadInputException extends CommonUseCaseException {
    public CommonBadInputException(String message) {
        super(message);
    }

    public CommonBadInputException(String message, Object details) {
        super(message, details);
    }
}
