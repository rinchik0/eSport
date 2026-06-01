package com.rinchik.esport.exception;

public class SteamAccountNotFoundException extends RuntimeException {
    public SteamAccountNotFoundException(String id) {
        super("Steam account not found with id: " + id);
    }
}
