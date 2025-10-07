package com.rinchik.esport.exception;

public class NotEventOrganizerException extends RuntimeException {
    public NotEventOrganizerException(Long userId, Long eventId) {
        super("User with id " + userId + " is not organizer of event with id " + eventId);
    }
}
