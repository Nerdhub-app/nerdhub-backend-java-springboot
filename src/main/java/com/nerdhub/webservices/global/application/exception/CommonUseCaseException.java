package com.nerdhub.webservices.global.application.exception;

import lombok.Getter;

@Getter
public abstract class CommonUseCaseException extends RuntimeException {
    protected Object details;

    public CommonUseCaseException(String message, Object details) {
        super(message);
        this.details = details;
    }

    public CommonUseCaseException(String message) {
        super(message);
    }
}
