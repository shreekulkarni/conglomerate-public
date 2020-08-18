package com.conglomerate.dev.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResetPinMismatchException extends ResponseStatusException {
    public ResetPinMismatchException() {
        super(HttpStatus.BAD_REQUEST, "The provided reset pin doesn't match the sent pin.");
    }
}
