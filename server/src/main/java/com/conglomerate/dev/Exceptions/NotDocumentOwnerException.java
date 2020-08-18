package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotDocumentOwnerException extends ResponseStatusException {
    public NotDocumentOwnerException(String username, int documentId) {
        super(HttpStatus.UNAUTHORIZED, "User " + username + " is not the owner of document with id " + documentId);
    }
}
