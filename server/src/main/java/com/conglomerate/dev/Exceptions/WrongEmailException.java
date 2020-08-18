package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WrongEmailException extends ResponseStatusException {

    public WrongEmailException(String username, String email) {
        super(HttpStatus.BAD_REQUEST, "The provided email " + email + " does not match the email on file for " + username);
    }
}
