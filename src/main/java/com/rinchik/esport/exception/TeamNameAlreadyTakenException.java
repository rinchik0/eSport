package com.rinchik.esport.exception;

public class TeamNameAlreadyTakenException extends RuntimeException {
    public TeamNameAlreadyTakenException(String name) {
        super("Team name " + name + " is already taken");
    }
}
