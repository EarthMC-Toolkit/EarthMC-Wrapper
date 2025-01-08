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

import java.util.concurrent.TimeUnit;

public class JSONRequest {
    static final okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(16, 3, TimeUnit.MINUTES))
            .addInterceptor(BrotliInterceptor.INSTANCE)
            .protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .build();

    public static final MediaType contentType = MediaType.parse("application/json; charset=utf-8");
    static final List<Integer> CODES = List.of(new Integer[]{ 200, 203, 304 });

    // Send GET request and get a JSON response back.
    public static @Nullable JsonElement sendGet(String url) {
        try {
            String res = execGet(url);
            return JsonParser.parseString(res);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Send POST request with JSON body and get a JSON response back.
    public static @Nullable JsonElement sendPost(String url, String body) {
        try {
            return JsonParser.parseString(execPost(url, body));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

//    @Contract("_, -> new")
//    public static @NotNull CompletableFuture<JsonElement> sendGetAsync(String url) {
//        return CompletableFuture.supplyAsync(() -> get(url)).exceptionally(ex -> {
//            System.out.println("Exception occurred!\n" + ex.getMessage());
//            throw new CompletionException(ex);
//        });
//    }
//
//    @Contract("_, _, -> new")
//    public static @NotNull CompletableFuture<JsonElement> sendPostAsync(String url, String body) {
//        return CompletableFuture.supplyAsync(() -> post(url, body)).exceptionally(ex -> {
//            System.out.println("Exception occurred!\n" + ex.getMessage());
//            throw new CompletionException(ex);
//        });
//    }

    static @NotNull String execGet(String url) throws APIException {
        try {
            okhttp3.Request req = builder.url(url).build();
            okhttp3.Response response = client.newCall(req).execute();

            return parseBody(response);
        } catch (Exception e) {
            throw new APIException("Failed GET request! Endpoint: " + url + "\n" + e.getMessage());
        }
    }

    static @NotNull String execPost(String url, String body) throws APIException {
        try {
            okhttp3.Request req = builder.url(url)
                .post(RequestBody.create(body, contentType))
                .build();

            okhttp3.Response response = client.newCall(req).execute();
            return parseBody(response);
        } catch (Exception e) {
            throw new APIException("Failed POST request!\n" + e.getMessage());
        }
    }

    static String parseBody(@NotNull Response response) throws APIException, IOException {
        int statusCode = response.code();
        if (!CODES.contains(statusCode)) {
            throw new APIException("API Error:\n  Response code: " + statusCode + "\n  Endpoint: " + response.request().url());
        }

        try (ResponseBody resBody = response.body()) {
            if (resBody == null) throw new APIException("Fetch Error: Response body is null");
            return resBody.string();
        }
    }
}