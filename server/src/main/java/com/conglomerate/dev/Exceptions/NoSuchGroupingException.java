package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchGroupingException extends ResponseStatusException {
    public NoSuchGroupingException(int groupingId) {
        super(HttpStatus.BAD_REQUEST, "Grouping with code: \"" + groupingId + "\" does not exist.");
    }
}
