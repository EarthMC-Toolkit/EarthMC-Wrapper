package net.emc.emcw.exceptions;

import java.io.Serial;

public class APIException extends Exception {
    @Serial
    private static final long serialVersionUID = -3129573277546383741L;

    public APIException() {
        super("An unknown API exception has occurred.");
    }

    public APIException(String message) {
        super(message);
    }
}
