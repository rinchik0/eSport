package com.rinchik.esport.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }
    public UserNotFoundException(String login) {
        super("User not found with login: " + login);
    }
}
