package com.rinchik.esport.exception;

import com.rinchik.esport.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            UserNotFoundException.class,
            TeamNotFoundException.class,
            EventNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e) {
        ErrorResponse response = new ErrorResponse("NOT FOUND", e.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({
            UserAlreadyEventParticipantException.class,
            UserAlreadyTeamMemberException.class,
            UserNotEventParticipantException.class,
            UserNotTeamMemberException.class,
            TeamNameAlreadyTakenException.class,
            LoginAlreadyTakenException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException e) {
        ErrorResponse response = new ErrorResponse("CONFLICT", e.getMessage(), HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({
            InvalidPasswordException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException e) {
        ErrorResponse response = new ErrorResponse("BAD REQUEST", e.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({
            NotEventOrganizerException.class,
            NotCaptainOfTeamException.class
    })
    public ResponseEntity<ErrorResponse> handleForbidden(RuntimeException e) {
        ErrorResponse response = new ErrorResponse("FORBIDDEN", e.getMessage(), HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            RoleNotMatchesGameException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
        ErrorResponse response = new ErrorResponse("VALIDATION_ERROR", e.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
