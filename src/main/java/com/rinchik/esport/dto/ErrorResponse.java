package com.rinchik.esport.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponse {
    private String error;
    private String message;
    private HttpStatus status;

    public ErrorResponse(String Error, String Message, HttpStatus Status) {
        error = Error;
        message = Message;
        status = Status;
    }
}
