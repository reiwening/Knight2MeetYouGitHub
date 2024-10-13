package com.g5.cs203proj.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NotEnoughPlayersException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotEnoughPlayersException(int numPlayers){
        super("You currently have " + numPlayers + " players.");
    }
}
