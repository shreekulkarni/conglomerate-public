package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoResetPinException extends ResponseStatusException {
    public NoResetPinException(String username) {
        super(HttpStatus.BAD_REQUEST, "There exists no valid reset pin for " + username + ". Did the pin expire?");
    }
}
