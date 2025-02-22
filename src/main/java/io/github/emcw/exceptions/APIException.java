package io.github.emcw.exceptions;

import lombok.Getter;
import okhttp3.HttpUrl;

@Getter
public class APIException extends Exception {
    private int statusCode = 0;
    private HttpUrl url = null;

//    public APIException() {
//        super("An unknown API exception has occurred.");
//    }

    public APIException(String msg) {
        super(msg);
    }

    public APIException(HttpUrl url, int statusCode) {
        super();

        this.url = url;
        this.statusCode = statusCode;
    }

    public String asString() {
        return String.format("API Error:\n  Response code: %d\n  URL: %s", statusCode, url);
    }
}