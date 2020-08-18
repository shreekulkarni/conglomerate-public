package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchFolderException extends ResponseStatusException {
    public NoSuchFolderException(int folderId) {
        super(HttpStatus.BAD_REQUEST, "Folder with id " + folderId + " does not exist.");
    }
}
