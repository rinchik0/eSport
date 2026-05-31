package com.rinchik.esport.exception;

public class UserIsMemberOfAnotherTeamException extends RuntimeException {
    public UserIsMemberOfAnotherTeamException(Long userId) {
        super("User with id " + userId + "is member of another team");
    }
}