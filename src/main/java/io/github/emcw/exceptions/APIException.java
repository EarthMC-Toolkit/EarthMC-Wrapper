package io.github.emcw.exceptions;

public class APIException extends Exception {
    public APIException() {
        super("An unknown API exception has occurred.");
    }

    public APIException(String message) {
        super(message);
    }
}