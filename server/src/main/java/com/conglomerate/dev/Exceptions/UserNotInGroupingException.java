package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotInGroupingException extends ResponseStatusException {
    public UserNotInGroupingException(String username, int groupingId) {
        super(HttpStatus.BAD_REQUEST, "User \"" + username + "\" is not a member in grouping #" + groupingId);
    }
}
