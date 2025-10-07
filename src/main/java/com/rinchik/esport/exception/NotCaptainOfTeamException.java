package com.rinchik.esport.exception;

public class NotCaptainOfTeamException extends RuntimeException {
    public NotCaptainOfTeamException(Long userId, Long teamId) {
        super("User with id " + userId + " is not captain of team with id " + teamId);
    }
}
