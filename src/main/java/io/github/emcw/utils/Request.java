package io.github.emcw.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.emcw.exceptions.APIException;

import lombok.SneakyThrows;

import okhttp3.*;
import okhttp3.brotli.BrotliInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
public class Request {
    static final okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(16, 3, TimeUnit.MINUTES))
            .addInterceptor(BrotliInterceptor.INSTANCE)
            .protocols(List.of(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .build();

    static final List<Integer> codes = List.of(new Integer[]{ 200, 203, 304 });
    static final String epUrl = "https://raw.githubusercontent.com/EarthMC-Toolkit/EarthMC-NPM/main/src/endpoints.json";
    static final Cache<String, JsonObject> endpoints = Caffeine.newBuilder().build();

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
        try { return send(epUrl); }
        catch (APIException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static <T> T send(String url) throws APIException {
        return (T) JsonParser.parseString(fetch(url));
    }

    @SneakyThrows
    public static @NotNull String fetch(String urlString) {
        builder.url(urlString);

        final Response response;
        String endpointStr = "\nEndpoint: " + urlString;

        try {
            response = client.newCall(builder.build()).execute();

            int statusCode = response.code();
            if (!codes.contains(statusCode))
                throw new APIException("API Error! Response code: " + statusCode + endpointStr);

            try (ResponseBody body = response.body()) {
                if (body == null) throw new APIException("Fetch Error: Response body is null");
                return body.string();
            }
        } catch (Exception e) {
            throw new APIException("Request failed! " + endpointStr + e.getMessage());
        }
    }
}