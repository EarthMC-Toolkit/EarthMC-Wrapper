package io.github.emcw.utils.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.emcw.utils.http.oapi.v3.RequestBodyV3;
import org.jetbrains.annotations.Nullable;

public class OfficialAPI {
    public static String DOMAIN = "https://api.earthmc.net";

    public static class V3 {
        static final String BASE_ENDPOINT = DOMAIN + "/v3/aurora";

        public static @Nullable JsonElement sendRequest(String endpoint, @Nullable RequestBodyV3 body) {
            String ep = BASE_ENDPOINT + endpoint;
            return body == null ? JSONRequest.sendGet(ep) : JSONRequest.sendPost(ep, body.asString());
        }

        public static @Nullable JsonObject serverInfo() {
            // Server info doesn't have its own endpoint, it just lives at the base.
            JsonElement res = sendRequest("", null);
            return res == null ? null : res.getAsJsonObject();
        }

        public static @Nullable JsonArray towns(String[] ids) {
            JsonElement res = sendRequest("/towns", new RequestBodyV3(ids));
            return res == null ? null : res.getAsJsonArray();
        }

        public static @Nullable JsonArray nations(String[] ids) {
            JsonElement res = sendRequest("/nations", new RequestBodyV3(ids));
            return res == null ? null : res.getAsJsonArray();
        }
    }

//    private @Nullable JsonObject townyData(@NotNull String endpoint) {
//        String fullUrl = OAPI_DOMAIN + (endpoint.startsWith("/") ? endpoint.substring(1) : endpoint);
//        CompletableFuture<JsonObject> data = get(fullUrl);
//
//        try {
//            return data.join().getAsJsonObject();
//        }
//        catch (Exception e) {
//            System.out.println(
//                "Error fetching OAPI data from " + fullUrl +
//                "\nReceived response may be incorrectly formatted."
//            );
//        }
//
//        return null;
//    }
}