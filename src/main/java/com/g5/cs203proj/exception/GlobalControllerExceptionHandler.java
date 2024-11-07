package com.g5.cs203proj.exception;

import java.util.Map;
import java.io.ObjectInputFilter.Status;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.g5.cs203proj.enums.*;


@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePlayerNotFoundException(PlayerNotFoundException ex) {
        // what happens if a PlayerNotFoundException is thrown
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Player not found");
        body.put("player id: ", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new HashMap<>();

        // More user-friendly and clear message
        String parameterName = ex.getName(); // This gives the name of the parameter that caused the error
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String actualValue = ex.getValue() != null ? ex.getValue().toString() : "null";

        // Custom message with specific information
        body.put("message",
                String.format("Invalid input for parameter '%s': expected a value of type '%s', but received '%s'.",
                        parameterName, expectedType, actualValue));

        // Keep the original error message for more technical debugging purposes
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // @ExceptionHandler(UsernameNotFoundException.class)
    // public ResponseEntity<Object> handleUsernameNotFound(UsernameNotFoundException ex){
    //     Map<String, Object> body = new HashMap<>();
    //     body.put("error: ","Username not found");
    //     body.put("username: ", ex.getMessage());
    //     return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    // }

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

//match exceptions
    // Handler for MatchNotFoundException
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMatchNotFoundException(MatchNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Handler for InvalidMatchWinnerException
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InvalidMatchWinnerException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidMatchWinnerException(InvalidMatchWinnerException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Handler for PlayerAlreadyInTournamentException
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(NotEnoughPlayersException.class)
    public ResponseEntity<Map<String, Object>> handleNotEnoughPlayersException(NotEnoughPlayersException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

//Tournament Exceptions
    // Handler for TournamentNotFoundException
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TournamentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTournamentNotFoundException(TournamentNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Handler for PlayerAlreadyInTournamentException
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PlayerAlreadyInTournamentException.class)
    public ResponseEntity<Map<String, Object>> handlePlayerAlreadyInTournamentException(PlayerAlreadyInTournamentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        // body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
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

    // Handler for PlayerNotInTournamentException
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PlayerNotInTournamentException.class)
    public ResponseEntity<Map<String, Object>> handlePlayerNotInTournamentException(PlayerNotInTournamentException ex) {
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

    // Handler for InvalidPlayerRangeException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPlayerRangeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPlayerRangeException(InvalidPlayerRangeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
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
