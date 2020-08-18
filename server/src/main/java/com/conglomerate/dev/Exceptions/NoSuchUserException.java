package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchUserException extends ResponseStatusException {
    public NoSuchUserException(String username) {
        super(HttpStatus.BAD_REQUEST, "User \"" + username + "\" does not exist.");
    }
}
