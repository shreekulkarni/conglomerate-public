package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotGroupOwnerException extends ResponseStatusException{
    public NotGroupOwnerException(String username) {
        super(HttpStatus.BAD_REQUEST, "User \"" + username + "\" is not the group owner");
    }
}
