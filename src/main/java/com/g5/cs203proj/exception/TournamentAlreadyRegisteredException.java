package com.g5.cs203proj.exception;

public class TournamentAlreadyRegisteredException extends RuntimeException{
    public TournamentAlreadyRegisteredException(String msg){
        super(msg);
    }
    
}
