package io.github.emcw.utils.parsers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.emcw.map.entities.Nation;
import io.github.emcw.map.entities.Player;
import io.github.emcw.map.entities.Resident;
import io.github.emcw.map.entities.Town;
import io.github.emcw.utils.http.SquaremapAPI;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static io.github.emcw.utils.GsonUtil.*;

public class SquaremapParser {
    static final Cache<String, JsonObject> rawTowns = buildEmpty();
    static final Cache<String, JsonObject> rawNations = buildEmpty();
    static final Cache<String, JsonObject> rawResidents = buildEmpty();
    static final Cache<String, JsonObject> rawPlayers = buildEmpty();

    private static final Cache<String, Town> towns = buildEmpty();
    private static final Cache<String, Nation> nations = buildEmpty();
    private static final Cache<String, Resident> residents = buildEmpty();
    private static final Cache<String, Player> players = buildEmpty();

    @Contract(" -> new")
    static <K, V> @NotNull Cache<K, V> buildEmpty() {
        return Caffeine.newBuilder().build();
    }

    public static void parseMapData() {
        parseMapData(true, true, true);
    }

    public static void parseMapData(Boolean parseTowns, Boolean parseNations, Boolean parseResidents) {
        JsonArray mapData = SquaremapAPI.mapData();
        if (mapData.size() < 1) return;
    }

    public static void parsePlayerData() {
        JsonArray pData = SquaremapAPI.playerData().getAsJsonArray("players");
        if (pData.size() < 1) return;

        rawPlayers.invalidateAll();

        arrAsStream(pData).forEach(p -> {
            JsonObject curPlayer = p.getAsJsonObject();
            String name = keyAsStr(curPlayer, "name");
            String uuid = keyAsStr(curPlayer, "uuid");

            rawPlayers.asMap().computeIfAbsent(name, k -> {
                JsonObject obj = new JsonObject();

                obj.addProperty("uuid", uuid);
                obj.addProperty("name", name);
                obj.addProperty("nickname", keyAsStr(curPlayer, "display_name"));
                obj.addProperty("world", keyAsStr(curPlayer, "world"));
                obj.addProperty("x", keyAsInt(curPlayer, "x"));
                obj.addProperty("z", keyAsInt(curPlayer, "z"));
                obj.addProperty("yaw", keyAsInt(curPlayer, "yaw"));

                return obj;
            });
        });
    }

    public static Cache<String, Town> parsedTowns() {
        return null;
    }

    public static Cache<String, Nation> parsedNations() {
        return null;
    }

    public static Cache<String, Resident> parsedResidents() {
        return null;
    }

    public static Cache<String, Player> parsedPlayers() {
        return null;
    }
}