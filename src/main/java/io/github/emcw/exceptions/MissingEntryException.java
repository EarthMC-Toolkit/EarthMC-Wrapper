package io.github.emcw.exceptions;

public class MissingEntryException extends Exception {
    public MissingEntryException() {
        super("No entries found! Make sure the map is properly populated.");
    }

    public MissingEntryException(String message) {
        super(message);
    }
}