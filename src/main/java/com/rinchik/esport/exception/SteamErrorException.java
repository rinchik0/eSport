package com.rinchik.esport.exception;

public class SteamErrorException extends RuntimeException {
    public SteamErrorException() {
        super("Steam error");
    }
}
