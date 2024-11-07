package com.g5.cs203proj.exception.player;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TooManyPlayersException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TooManyPlayersException(int numPlayers) {
        super("The tournament currently has " + numPlayers + " players. The maximum allowed for a round-robin format is 16.");
    }
}
