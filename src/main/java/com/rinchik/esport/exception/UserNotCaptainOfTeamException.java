package com.rinchik.esport.exception;

public class UserNotCaptainOfTeamException extends RuntimeException {
    public UserNotCaptainOfTeamException(Long userId, Long teamId) {
        super("User with id " + userId + " is not captain of team with id " + teamId);
    }
}
