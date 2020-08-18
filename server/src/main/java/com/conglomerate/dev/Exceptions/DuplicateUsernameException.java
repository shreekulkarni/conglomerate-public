package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateUsernameException extends ResponseStatusException {
    public DuplicateUsernameException() {
        super(HttpStatus.BAD_REQUEST, "This username is already being used by another account.");
    }
}
