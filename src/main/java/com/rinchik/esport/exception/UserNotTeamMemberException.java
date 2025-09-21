package com.rinchik.esport.exception;

public class UserNotTeamMemberException extends RuntimeException {
    public UserNotTeamMemberException(Long userId, Long teamId) {
        super("User with id " + userId + " is not member of team with id " + teamId);
    }
}
