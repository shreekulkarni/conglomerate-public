package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DocumentUploadException extends ResponseStatusException {
    public DocumentUploadException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong when uploading the file.");
    }
}
