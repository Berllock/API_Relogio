package com.dev.watchapi.exception;

import java.time.Instant;
import java.util.List;

public record ErroApi(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List <ErroCampo> fieldError
) {

    public record ErroCampo(String field, String message) {

    }
}
