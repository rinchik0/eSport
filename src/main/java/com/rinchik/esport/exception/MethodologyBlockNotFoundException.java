package com.rinchik.esport.exception;

public class MethodologyBlockNotFoundException extends RuntimeException {
    public MethodologyBlockNotFoundException(Long id) {
        super("MethodologyBlock not found with id: " + id);
    }
}
