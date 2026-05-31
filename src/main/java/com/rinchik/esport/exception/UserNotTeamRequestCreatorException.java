package com.rinchik.esport.exception;

public class UserNotTeamRequestCreatorException extends RuntimeException {
    public UserNotTeamRequestCreatorException(Long userId, Long requestId) {
        super("User with id " + userId + " is not creator of TeamRequest with id " + requestId);
    }
}
