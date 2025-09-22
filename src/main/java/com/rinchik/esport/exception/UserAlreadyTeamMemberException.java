package com.rinchik.esport.exception;

public class UserAlreadyTeamMemberException extends RuntimeException {
    public UserAlreadyTeamMemberException(Long userId, Long teamId) {
        super("User with id " + userId + "is already member of team with id " + teamId);
    }
}
