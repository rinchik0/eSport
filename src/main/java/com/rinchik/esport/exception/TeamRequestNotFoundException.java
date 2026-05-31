package com.rinchik.esport.exception;

public class TeamRequestNotFoundException extends RuntimeException {
    public TeamRequestNotFoundException(Long id) {
        super("TeamRequest not found with id: " + id);
    }
}
