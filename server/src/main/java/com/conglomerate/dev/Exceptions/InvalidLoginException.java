package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidLoginException extends ResponseStatusException {
    public InvalidLoginException() {
        super(HttpStatus.BAD_REQUEST, "The username or password does not match.");
    }
}
