package com.g5.cs203proj.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TournamentFullException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TournamentFullException(Long id) {
        super("Tournament " + id + " is full");
    }
}