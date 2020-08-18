package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateEmailException extends ResponseStatusException {
    public DuplicateEmailException() {
        super(HttpStatus.BAD_REQUEST, "This email is being used by another account.");
    }
}
