package com.rinchik.esport.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Wrong password");
    }
}
