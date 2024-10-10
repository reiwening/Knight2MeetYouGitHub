package com.g5.cs203proj.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TournamentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TournamentNotFoundException(Long id) {
        super("Could not find tournament " + id);
    }
}
