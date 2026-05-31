package com.rinchik.esport.exception;

public class MethodologyNotFoundException extends RuntimeException {
    public MethodologyNotFoundException(Long id) {
        super("Methodology not found with id: " + id);
    }
}
