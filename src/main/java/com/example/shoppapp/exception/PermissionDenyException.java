package com.example.shoppapp.exception;

public class PermissionDenyException extends Exception{
    public PermissionDenyException() {
    }

    public PermissionDenyException(String message) {
        super(message);
    }
}
