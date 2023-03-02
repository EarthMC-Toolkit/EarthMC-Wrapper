package io.github.emcw.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.emcw.exceptions.APIException;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@NoArgsConstructor
public class Request {
    private static final HttpClient client = HttpClient.newHttpClient();
    static List<Integer> codes = List.of(new Integer[]{ 200, 203, 304 });

    static final String epUrl = "https://raw.githubusercontent.com/EarthMC-Toolkit/EarthMC-NPM/master/endpoints.json";
    static JsonObject endpoints;

    @SuppressWarnings("unchecked")
    public static <T> T send(String url) throws APIException {
        return (T) JsonParser.parseString(fetch(url));
    }

    static JsonObject getEndpoints() {
        if (endpoints == null) endpoints = updateEndpoints();
        return endpoints;
    }

    static JsonObject updateEndpoints() {
        try { return send(epUrl); }
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

            try { response = client.sendAsync(req, BodyHandlers.ofString(StandardCharsets.UTF_8)).join(); }
            catch(Exception e) {
                throw new APIException("Request failed! " + endpointStr + e.getMessage());
            }

            int statusCode = response.statusCode();
            if (!codes.contains(statusCode))
                throw new APIException("API Error! Response code: " + statusCode + endpointStr);

            return response.body();
        } catch (Exception e) { throw new APIException(e.getMessage()); }
    }
}