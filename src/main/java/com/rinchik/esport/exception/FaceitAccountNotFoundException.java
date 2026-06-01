package com.rinchik.esport.exception;

public class FaceitAccountNotFoundException extends RuntimeException {
    public FaceitAccountNotFoundException(String nickname) {
        super("Faceit account not found with nickname: " + nickname);
    }
    public FaceitAccountNotFoundException(Long id) {
        super("Faceit account not found with id: " + id);
    }
}
