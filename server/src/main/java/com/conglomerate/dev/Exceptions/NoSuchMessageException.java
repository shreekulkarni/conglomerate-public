package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchMessageException extends ResponseStatusException {
    public NoSuchMessageException(int messageId) {
        super(HttpStatus.BAD_REQUEST, "Message with ID \"" + messageId + "\" does not exist.");
    }
}
