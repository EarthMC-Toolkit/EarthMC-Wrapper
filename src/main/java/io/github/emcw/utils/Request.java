package io.github.emcw.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.emcw.exceptions.APIException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class Request {
    private static final HttpClient client = HttpClient.newHttpClient();
    static List<Integer> codes = List.of(new Integer[]{ 200, 203, 304 });

    static JsonObject endpoints;
    String url;

    Request(String endpoint) {
        this.url = endpoint;
        endpoints = getEndpoints();
    }

    public <T> T body() throws APIException {
        return Request.bodyOf(this.url);
    }

    @SuppressWarnings("unchecked")
    public static <T> T bodyOf(String url) throws APIException {
        return (T) JsonParser.parseString(fetch(url));
    }

    static JsonObject getEndpoints() {
        if (endpoints == null) endpoints = updateEndpoints();
        return endpoints;
    }

    static JsonObject updateEndpoints() {
        final String url = "https://raw.githubusercontent.com/EarthMC-Toolkit/EarthMC-NPM/master/endpoints.json";
        try { return Request.bodyOf(url); }
        catch (APIException e) {
            return null;
        }
    }

    static String fetch(String urlString) throws APIException {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .timeout(Duration.ofSeconds(5))
                    .GET().build();

            final HttpResponse<String> response;
            String endpointStr = "\nEndpoint: " + urlString;

            try { response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)); }
            catch (HttpTimeoutException e) { throw new APIException("Request timed out after 5 seconds." + endpointStr); }

            int statusCode = response.statusCode();
            if (!codes.contains(statusCode))
                throw new APIException("API Error! Response code: " + statusCode + endpointStr);

            return response.body();
        } catch (Exception e) { throw new APIException(e.getMessage()); }
    }
}
