package io.github.emcw.utils.http;

import com.google.gson.JsonElement;
import io.github.emcw.exceptions.APIException;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.*;
import okhttp3.brotli.BrotliInterceptor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

import java.util.concurrent.TimeUnit;

public class JSONRequest {
    public static final MediaType contentType = MediaType.parse("application/json; charset=utf-8");

    static final okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(16, 3, TimeUnit.MINUTES))
            .addInterceptor(BrotliInterceptor.INSTANCE)
            .protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .build();

    static final List<Integer> CODES = List.of(new Integer[]{ 200, 203, 304 });
    static final String ENDPOINTS_URL = "https://raw.githubusercontent.com/EarthMC-Toolkit/EarthMC-NPM/main/src/endpoints.json";
    static final Cache<String, JsonObject> endpoints = Caffeine.newBuilder().build();

    @SuppressWarnings("SameReturnValue")
    static Cache<String, JsonObject> getEndpoints() {
        if (endpoints.asMap().isEmpty()) {
            JsonObject eps = updateEndpoints();
            if (eps != null) {
                eps.asMap().forEach((k, v) -> endpoints.put(k, v.getAsJsonObject()));
            }
        }

        return endpoints;
    }

    static @Nullable JsonObject updateEndpoints() {
        JsonElement el = sendGet(ENDPOINTS_URL);
        if (el == null) {
            return null;
        }

        return el.getAsJsonObject();
    }

    // Send GET request and get a JSON response back.
    public static @Nullable JsonElement sendGet(String url) {
        try {
            return JsonParser.parseString(execGet(url));
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
//    public static @NotNull CompletableFuture<JsonElement> getAsync(String url) {
//        return CompletableFuture.supplyAsync(() -> get(url)).exceptionally(ex -> {
//            System.out.println("Exception occurred!\n" + ex.getMessage());
//            throw new CompletionException(ex);
//        });
//    }
//
//    @Contract("_, _, -> new")
//    public static @NotNull CompletableFuture<JsonElement> postAsync(String url, String body) {
//        return CompletableFuture.supplyAsync(() -> post(url, body)).exceptionally(ex -> {
//            System.out.println("Exception occurred!\n" + ex.getMessage());
//            throw new CompletionException(ex);
//        });
//    }

    static @NotNull String execGet(String url) throws APIException {
        String endpointStr = "\n[GET] Endpoint: " + url;

        try {
            okhttp3.Request req = builder.url(url).build();
            okhttp3.Response response = client.newCall(req).execute();

            return parseBody(response);
        } catch (Exception e) {
            throw new APIException("Request failed! " + endpointStr + e.getMessage());
        }
    }

    static @NotNull String execPost(String url, String body) throws APIException {
        String endpointStr = "\n[POST] Endpoint: " + url;

        try {
            RequestBody reqBody = RequestBody.create(body, contentType);

            okhttp3.Request req = builder.url(url).post(reqBody).build();
            okhttp3.Response response = client.newCall(req).execute();

            return parseBody(response);
        } catch (Exception e) {
            throw new APIException("Request failed! " + endpointStr + e.getMessage());
        }
    }

    static String parseBody(@NotNull Response response) throws APIException, IOException {
        int statusCode = response.code();
        if (!CODES.contains(statusCode)) {
            throw new APIException("API Error! Response code: " + statusCode + response.request().url());
        }

        try (ResponseBody resBody = response.body()) {
            if (resBody == null) throw new APIException("Fetch Error: Response body is null");
            return resBody.string();
        }
    }
}