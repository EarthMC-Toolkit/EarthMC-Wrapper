package io.github.emcw.utils.http;

import com.google.gson.JsonElement;
import io.github.emcw.exceptions.APIException;

import com.google.gson.JsonParser;

import okhttp3.*;
import okhttp3.brotli.BrotliInterceptor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class JSONRequest {
    private static final OkHttpClient client = new OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS)
        .connectionPool(new ConnectionPool(16, 3, TimeUnit.MINUTES))
        .addInterceptor(BrotliInterceptor.INSTANCE)
        .protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .build();

    static final okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
    public static final MediaType contentType = MediaType.parse("application/json; charset=utf-8");

    @SuppressWarnings("unused")
    public static class ASYNC {
        public static JsonElement sendGet(String url) {
            okhttp3.Request req = builder.url(url).build();
            return trySendAndParse(req);
        }

        public static JsonElement sendPost(String url, String body) {
            okhttp3.Request req = builder.url(url)
                .post(RequestBody.create(body, contentType))
                .build();

            return trySendAndParse(req);
        }

        public static JsonElement trySendAndParse(okhttp3.Request req) {
            OkHttpCallback cbFut = new OkHttpCallback();
            client.newCall(req).enqueue(cbFut);

            try {
                okhttp3.Response res = cbFut.join();
                return JsonParser.parseString(parseBody(res));
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return null;
            }
        }
    }

    /** Send GET request and get a JSON response back. */
    public static @Nullable JsonElement sendGet(@NotNull String url) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return JsonParser.parseString(execGet(url));
                } catch (APIException e) {
                    throw new RuntimeException(e);
                }
            }).join();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /** Send POST request with a body and get a JSON response back. */
    public static @Nullable JsonElement sendPost(@NotNull String url, String body) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return JsonParser.parseString(execPost(url, body));
                } catch (APIException e) {
                    throw new RuntimeException(e);
                }
            }).join();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    static @NotNull String execGet(String url) throws APIException {
        try {
            okhttp3.Request req = builder.url(url)
                .get()
                .build();

            okhttp3.Response response = client.newCall(req).execute();
            return parseBody(response);
        }
        catch (APIException e) {
            throw new APIException("Failed GET request! " + e.asString());
        }
        catch (Exception e) {
            throw new APIException("Failed GET request!\n" + e.getMessage());
        }
    }

    static @NotNull String execPost(String url, String body) throws APIException {
        try {
            okhttp3.Request req = builder.url(url)
                .post(RequestBody.create(body, contentType))
                .build();

            okhttp3.Response response = client.newCall(req).execute();
            return parseBody(response);
        }
        catch (APIException e) {
            throw new APIException("Failed POST request! " + e.asString());
        }
        catch (Exception e) {
            throw new APIException("Failed POST request!\n" + e.getMessage());
        }
    }

    static @NotNull String parseBody(@NotNull Response res) throws APIException, IOException {
        if (!res.isSuccessful()) {
            throw new APIException(res);
        }

        try (ResponseBody body = res.body()) {
            if (body == null) throw new APIException("Could not parse null response body.");
            return body.string();
        }
    }
}