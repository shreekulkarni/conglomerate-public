package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchDocumentException extends ResponseStatusException {
    public NoSuchDocumentException(int documentId) {
        super(HttpStatus.BAD_REQUEST, "The document with id " + documentId + " does not exist.");
    }
}
