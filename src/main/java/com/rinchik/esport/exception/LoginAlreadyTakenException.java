package com.rinchik.esport.exception;

public class LoginAlreadyTakenException extends RuntimeException {
    public LoginAlreadyTakenException(String login) {
        super("Login" + login + " is already taken");
    }
}
