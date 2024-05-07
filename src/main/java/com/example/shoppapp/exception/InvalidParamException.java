package com.example.shoppapp.exception;

public class InvalidParamException extends Exception{
    public InvalidParamException() {
    }

    public InvalidParamException(String message) {
        super(message);
    }
}
