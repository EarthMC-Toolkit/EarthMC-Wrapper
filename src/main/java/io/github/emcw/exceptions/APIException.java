package io.github.emcw.exceptions;

import lombok.Getter;
import okhttp3.HttpUrl;

@Getter
public class APIException extends Exception {
    private HttpUrl url = null;
    private Integer statusCode = null;
    private String message = null;

//    public APIException() {
//        super("An unknown API exception has occurred.");
//    }

    public APIException(String msg) {
        super(msg);
    }

    public APIException(HttpUrl url, int statusCode, String msg) {
        super();

        this.url = url;
        this.statusCode = statusCode;
        this.message = msg;
    }

    public String asString() {
        StringBuilder sb = new StringBuilder("API Error:\n");

        if (this.url != null) {
            sb.append("  URL: ").append(this.url).append("\n");
        }

        if (this.statusCode != null) {
            sb.append("  Response Code: ").append(this.statusCode).append("\n");
        }

        if (this.message != null) {
            sb.append("  Response Message: ").append(this.message);
        }

        return sb.toString();
    }
}