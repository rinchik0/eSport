package com.rinchik.esport.exception;

public class UserNotMethodologyAuthorException extends RuntimeException {
    public UserNotMethodologyAuthorException(Long userId, Long methId) {
        super("User with id " + userId + " is not author of methodology with id " + methId);
    }
}
