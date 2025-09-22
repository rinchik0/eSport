package com.rinchik.esport.exception;

public class UserNotEventParticipantException extends RuntimeException {
    public UserNotEventParticipantException(Long userId, Long eventId) {
        super("User with id " + userId + " is not participant of event with id " + eventId);
    }
}
