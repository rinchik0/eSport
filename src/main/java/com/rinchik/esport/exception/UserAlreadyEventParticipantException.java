package com.rinchik.esport.exception;

public class UserAlreadyEventParticipantException extends RuntimeException {
    public UserAlreadyEventParticipantException(Long userId, Long eventId) {
        super("User with id " + userId + " is already participant of event with id " + eventId);
    }
}
