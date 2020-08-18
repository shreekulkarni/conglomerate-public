package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotAMemberException extends ResponseStatusException {
    public NotAMemberException(String username, int groupingId) {
        super(HttpStatus.BAD_REQUEST, "User " + username + " not in group " + groupingId);
    }
}
