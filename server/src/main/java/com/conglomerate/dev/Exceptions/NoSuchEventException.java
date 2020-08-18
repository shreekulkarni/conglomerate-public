package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchEventException extends ResponseStatusException {
    public NoSuchEventException(int eventId) {
        super(HttpStatus.BAD_REQUEST, "The event with id " + eventId + " does not exist.");
    }
}
