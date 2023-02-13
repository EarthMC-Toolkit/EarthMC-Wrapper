package net.emc.emcw.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.emc.emcw.exceptions.APIException;
import static java.net.http.HttpResponse.BodyHandlers;

public class APIHelper {
    private static final HttpClient client = HttpClient.newHttpClient();
    static List<Integer> codes = List.of(new Integer[]{ 200, 203, 304 });

    public static CompletableFuture<JsonArray> getArray() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return (JsonArray) JsonParser.parseString(jsonReq(""));
            }
            catch (APIException e) {
                return new JsonArray();
            }
        });
    }

//    public static JsonObject getOnlinePlayer(String name) {
//        JsonArray ops = getOnlinePlayers().join().getAsJsonArray();
//        JsonObject pl = new JsonObject();
//
//        if (!ops.isEmpty()) {
//            for (JsonElement op : ops) {
//                JsonObject cur = op.getAsJsonObject();
//
//                if (Objects.equals(cur.get("name").getAsString(), name)) {
//                    pl = cur;
//                    break;
//                }
//            }
//        }
//
//        return pl;
//    }

    private static String jsonReq(String urlString) throws APIException {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .timeout(Duration.ofSeconds(5))
                    .GET().build();

            final HttpResponse<String> response;

            try { response = client.send(req, BodyHandlers.ofString(StandardCharsets.UTF_8)); }
            catch (HttpTimeoutException e) { throw new APIException("Request timed out after 5 seconds.\nEndpoint: " + urlString); }

            if (!codes.contains(response.statusCode()))
                throw new APIException("API Error! Response code: " + response.statusCode() + "\nEndpoint: " + urlString);

            return response.body();
        } catch (Exception e) { throw new APIException(e.getMessage()); }
    }
}

