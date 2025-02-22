package io.github.emcw.utils.http;

import com.google.gson.JsonElement;
import io.github.emcw.exceptions.APIException;

import com.google.gson.JsonParser;

import okhttp3.*;
import okhttp3.brotli.BrotliInterceptor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class JSONRequest {
    static final okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
    private static final OkHttpClient client = new OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS)
        .connectionPool(new ConnectionPool(16, 3, TimeUnit.MINUTES))
        .addInterceptor(BrotliInterceptor.INSTANCE)
        .protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .build();

    static final String MOCK_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
        "AppleWebKit/537.36 (KHTML, like Gecko) " +
        "Chrome/131.0.0.0 Safari/537.36 OPR/116.0.0.0 (Edition std-1)";

    public static final MediaType contentType = MediaType.parse("application/json; charset=utf-8");
    //static final List<Integer> CODES = List.of(new Integer[]{ 200, 203, 304 });

    @Contract("_, -> new")
    public static @NotNull CompletableFuture<JsonElement> sendGetAsync(String url) {
        return CompletableFuture.supplyAsync(() -> sendGet(url)).exceptionally(cause -> {
            //System.err.println("Exception occurred!\n" + ex.getMessage());
            throw new CompletionException(cause);
        });
    }

    @Contract("_, _, -> new")
    public static @NotNull CompletableFuture<JsonElement> sendPostAsync(String url, String body) {
        return CompletableFuture.supplyAsync(() -> sendPost(url, body)).exceptionally(cause -> {
            //System.err.println("Exception occurred!\n" + ex.getMessage());
            throw new CompletionException(cause);
        });
    }

    // Send GET request and get a JSON response back.
    public static @Nullable JsonElement sendGet(@NotNull String url) {
        try {
            String res = execGet(url);
            return JsonParser.parseString(res);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    // Send POST request with JSON body and get a JSON response back.
    public static @Nullable JsonElement sendPost(@NotNull String url, String body) {
        try {
            return JsonParser.parseString(execPost(url, body));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    static @NotNull String execGet(String url) throws APIException {
        try {
            okhttp3.Request req = builder.url(url)
                .addHeader("User-Agent", MOCK_AGENT)
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
                .addHeader("User-Agent", MOCK_AGENT)
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

    static String parseBody(@NotNull Response response) throws APIException, IOException {
        int statusCode = response.code();
        if (!response.isSuccessful()) {
            throw new APIException(response.request().url(), statusCode);
        }

        try (ResponseBody resBody = response.body()) {
            if (resBody == null) throw new APIException("Could not parse null response body.");
            return resBody.string();
        }
    }
}