package io.github.emcw.utils.http;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class OfficialAPI {
    public OfficialAPI() { }

    static final String OAPI_DOMAIN = "https://api.earthmc.net/v2/aurora/";

    @Contract("_, -> new")
    private static @NotNull CompletableFuture<JsonObject> get(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Request.send(url);
            }
            catch (Exception e) {
                System.out.println("Exception occurred!\n" + e.getMessage());
                return null;
            }
        });
    }

    private @Nullable JsonObject townyData(@NotNull String endpoint) {
        String fullUrl = OAPI_DOMAIN + (endpoint.startsWith("/") ? endpoint.substring(1) : endpoint);
        CompletableFuture<JsonObject> data = get(fullUrl);

        try {
            return data.join().getAsJsonObject();
        }
        catch (Exception e) {
            System.out.println(
                "Error fetching OAPI data from " + fullUrl +
                "\nReceived response may be incorrectly formatted."
            );
        }

        return null;
    }

    public JsonObject serverInfo() {
        // TODO: Proper deserialization of API response. (Using records?)
        //return GsonUtil.deserialize(townyData(""));

        return null;
    }

    protected static class Towns {

    }
}