package com.g5.cs203proj.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TournamentAlreadyRegisteredException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    
    public TournamentAlreadyRegisteredException(String msg){
        super(msg);
    }
}
