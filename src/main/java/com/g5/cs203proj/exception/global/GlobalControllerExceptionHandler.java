package com.g5.cs203proj.exception.global;

import java.util.Map;
import java.time.DateTimeException;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.g5.cs203proj.enums.*;
import com.g5.cs203proj.exception.inputs.InvalidEloValueException;
import com.g5.cs203proj.exception.inputs.InvalidStatusException;
import com.g5.cs203proj.exception.inputs.InvalidStyleException;
import com.g5.cs203proj.exception.match.MatchNotFoundException;
import com.g5.cs203proj.exception.player.PlayerAvailabilityException;
import com.g5.cs203proj.exception.player.PlayerRangeException;
import com.g5.cs203proj.exception.tournament.TournamentAlreadyRegisteredException;
import com.g5.cs203proj.exception.tournament.TournamentFullException;
import com.g5.cs203proj.exception.tournament.TournamentNotFoundException;
import com.g5.cs203proj.exception.tournament.TournamentNotInRegistrationException;


@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new HashMap<>();

        
        String parameterName = ex.getName(); 
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String actualValue = ex.getValue() != null ? ex.getValue().toString() : "null";

       
        body.put("message",
                String.format("Invalid input for parameter '%s': expected a value of type '%s', but received '%s'.",
                        parameterName, expectedType, actualValue));

       
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleForbiddenRequest(AccessDeniedException ex){
        Map<String, Object> body = new HashMap<>();
        body.put("error", "forbidden");
        body.put("details", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex){
        Map<String, Object> body = new HashMap<>();
        body.put("error", "invalid inputs");
        body.put("details", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    // Handler for MatchNotFoundException
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMatchNotFoundException(MatchNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    
    // Handler for TournamentNotFoundException
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TournamentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTournamentNotFoundException(TournamentNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    
    // Generic handler for PlayerAvailabilityException
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PlayerAvailabilityException.class)
    public ResponseEntity<Map<String, Object>> handlePlayerAvailabilityException(PlayerAvailabilityException ex) {
        Map<String, Object> body = new HashMap<>();
        
        switch (ex.getType()) {
            case NOT_FOUND -> {
                body.put("error", "Player not found");
                body.put("details", ex.getMessage());
            }
            case NOT_IN_TOURNAMENT -> {
                body.put("error", "Player not in tournament");
                body.put("details", ex.getMessage());
            }
            case ALREADY_IN_TOURNAMENT -> {
                body.put("error", "Player already in tournament");
                body.put("details", ex.getMessage());
            }
        }
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(PlayerRangeException.class)
    public ResponseEntity<Map<String, Object>> handlePlayerRangeException(PlayerRangeException ex) {
        Map<String, Object> body = new HashMap<>();
        
        switch (ex.getRangeErrorType()) {
            case NOT_ENOUGH_PLAYERS -> body.put("error", "Not enough players in the tournament");
            case TOO_MANY_PLAYERS -> body.put("error", "Too many players in the tournament");
            case INVALID_RANGE -> body.put("error", "Invalid player range specified");
        }
        
        body.put("details", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Handler for PlayerAlreadyInTournamentException
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(TournamentAlreadyRegisteredException.class)
    public ResponseEntity<Map<String, Object>> handleTournamentAlreadyRegisteredException(TournamentAlreadyRegisteredException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        // body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Handler for TournamentFullException
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(TournamentFullException.class)
    public ResponseEntity<Map<String, Object>> handleTournamentFullException(TournamentFullException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }


    // Handler for InvalidEloValueException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidEloValueException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidEloValueException(InvalidEloValueException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Handler for InvalidStatusException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatusException(InvalidStatusException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        body.put("valid statuses", Statuses.getValidStatuses());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Handler for InvalidStyleException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidStyleException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStyleException(InvalidStyleException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        body.put("valid styles", Styles.getValidStyles());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<Map<String, Object>> handleDateTimeException(DateTimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TournamentNotInRegistrationException.class)
    public ResponseEntity<Map<String, Object>> handleTournamentNotInRegistrationException(TournamentNotInRegistrationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }
}
