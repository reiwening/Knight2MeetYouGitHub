package com.g5.cs203proj.exception.tournament;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RankingNotFound extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RankingNotFound(String username) {
        super("Could not find ranking for " + username);
    }
}
