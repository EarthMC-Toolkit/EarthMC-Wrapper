package io.github.emcw;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.emcw.oapi.v3.Point2D;
import io.github.emcw.utils.http.JSONRequest;

import io.github.emcw.oapi.v3.DiscordReqObj;
import io.github.emcw.oapi.v3.RequestBodyV3;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper around the official <a href="https://earthmc.net/docs/api">EarthMC API</a>.
 * @see V3
 */
@SuppressWarnings("unused")
public class OfficialAPI {
    public static String DOMAIN = "https://api.earthmc.net";

    private static String formattedUrl(String endpoint) {
        // Remove all leading slashes, ensuring only one exists.
        return DOMAIN + "/" + endpoint.replaceAll("^/+", "");
    }

    public enum Maps {
        AURORA
    }

    /**
     * Simple class for interacting with the 3rd version of the API.<br>
     * @see <a href="https://api.earthmc.net/v3/aurora/">api.earthmc.net/v3/aurora/</a>
     */

    // TODO: Add support for templates
    public static class V3 {
        public final String MAP_ENDPOINT;

        public V3(Maps map) {
            this.MAP_ENDPOINT = "/v3/" + map.name().toLowerCase();
        }

        /**
         * Sends a GET request to the given endpoint.<br><br>
         * To send a POST request, provide a {@link RequestBodyV3} as the second argument.
         * @param endpoint The endpoint after the domain. Example: "/towns"
         * @see #sendRequest(String, RequestBodyV3)
         * @return The received response as a base element. See {@link JsonElement}.
         */
        public @Nullable JsonElement sendRequest(String endpoint) {
            return JSONRequest.sendGet(formattedUrl(MAP_ENDPOINT + endpoint));
        }

        /**
         * Sends a POST request to the given endpoint with a valid body.
         * @param endpoint The endpoint after the domain. Example: "/towns"
         * @param body The body to send along with the request - the schema must match what is required for the endpoint.
         * @return The received response as a base element. See {@link JsonElement}.
         */
        public JsonElement sendRequest(String endpoint, @NotNull RequestBodyV3 body) {
            return JSONRequest.sendPost(formattedUrl(MAP_ENDPOINT + endpoint), body.asString());
        }

        public @Nullable JsonObject serverInfo() {
            // Server info doesn't have its own endpoint, it just lives at the base.
            JsonElement res = sendRequest("");
            return res == null ? null : res.getAsJsonObject();
        }

        public @Nullable JsonArray discord(DiscordReqObj[] objs) {
            JsonElement res = sendRequest("/players", new RequestBodyV3(objs));
            return res == null ? null : res.getAsJsonArray();
        }

        // Consider restricting this to two values (Point2D).
        public @Nullable JsonArray location(Point2D[] points) {
            JsonElement res = sendRequest("/location", new RequestBodyV3(points));
            return res == null ? null : res.getAsJsonArray();
        }

        //#region Towns
        public @Nullable JsonArray townsList() {
            JsonElement res = sendRequest("/towns");
            return res == null ? null : res.getAsJsonArray();
        }

        public @Nullable JsonArray towns(String[] ids) {
            JsonElement res = sendRequest("/towns", new RequestBodyV3(ids));
            return res == null ? null : res.getAsJsonArray();
        }
        //#endregion

        //#region Nations
        public @Nullable JsonArray nationsList() {
            JsonElement res = sendRequest("/nations");
            return res == null ? null : res.getAsJsonArray();
        }

        public @Nullable JsonArray nations(String[] ids) {
            JsonElement res = sendRequest("/nations", new RequestBodyV3(ids));
            return res == null ? null : res.getAsJsonArray();
        }
        //#endregion

        //#region Players
        public @Nullable JsonArray playersList() {
            JsonElement res = sendRequest("/players");
            return res == null ? null : res.getAsJsonArray();
        }

        public @Nullable JsonArray players(String[] ids) {
            JsonElement res = sendRequest("/players", new RequestBodyV3(ids));
            return res == null ? null : res.getAsJsonArray();
        }
        //#endregion

        //#region Quarters
        public @Nullable JsonArray quartersList() {
            JsonElement res = sendRequest("/quarters");
            return res == null ? null : res.getAsJsonArray();
        }

        public @Nullable JsonArray quarters(String[] uuids) {
            JsonElement res = sendRequest("/quarters", new RequestBodyV3(uuids));
            return res == null ? null : res.getAsJsonArray();
        }
        //#endregion
    }
}