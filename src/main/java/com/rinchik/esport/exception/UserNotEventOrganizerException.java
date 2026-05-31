package com.rinchik.esport.exception;

public class UserNotEventOrganizerException extends RuntimeException {
    public UserNotEventOrganizerException(Long userId, Long eventId) {
        super("User with id " + userId + " is not organizer of event with id " + eventId);
    }
}
