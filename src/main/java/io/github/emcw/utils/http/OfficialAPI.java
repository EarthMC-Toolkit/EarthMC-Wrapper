package io.github.emcw.utils.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class OfficialAPI {
    public static String DOMAIN = "https://api.earthmc.net";

    public static class V3 {
        static final String BASE_ENDPOINT = DOMAIN + "/v3/aurora/";

        public static @Nullable JsonObject serverInfo() {
            // Server info doesn't have its own endpoint, it just lives at the base.
            JsonElement res = JSONRequest.sendGet(BASE_ENDPOINT);
            if (res == null) {
                return null;
            }

            return res.getAsJsonObject();
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