package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AlreadyRSVPedException extends ResponseStatusException {
    public AlreadyRSVPedException(String username) {
        super(HttpStatus.BAD_REQUEST, "User \"" + username + "\" has already RSVPed.");
    }
}
