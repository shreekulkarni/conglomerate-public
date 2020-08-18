package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyInGroupingException extends ResponseStatusException {
    public UserAlreadyInGroupingException(String username) {
        super(HttpStatus.BAD_REQUEST, "User \"" + username + "\" is already a member.");
    }
}
