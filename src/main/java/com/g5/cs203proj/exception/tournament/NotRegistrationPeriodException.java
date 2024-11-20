package com.g5.cs203proj.exception.tournament;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NotRegistrationPeriodException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public NotRegistrationPeriodException(String msg){
        super(msg);
    }
}
