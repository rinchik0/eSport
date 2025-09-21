package com.rinchik.esport.exception;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(Long id) {
        super("Team not found with id: " + id);
    }
}
