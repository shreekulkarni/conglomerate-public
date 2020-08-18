package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidAuthTokenException extends ResponseStatusException {
    public InvalidAuthTokenException() {
        super(HttpStatus.BAD_REQUEST, "The provided auth token is invalid");
    }
}
