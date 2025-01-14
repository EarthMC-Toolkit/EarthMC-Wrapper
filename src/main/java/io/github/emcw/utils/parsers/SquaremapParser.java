package io.github.emcw.utils.parsers;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import com.github.benmanes.caffeine.cache.Cache;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.emcw.map.entities.*;
import io.github.emcw.squaremap.SquaremapAPI;
import io.github.emcw.squaremap.ProcessedMarker;

import static io.github.emcw.utils.GsonUtil.*;

// TODO: Remove annotation when the class is fully complete.
@SuppressWarnings("unused")
public class SquaremapParser extends BaseParser {
    // TODO: Maybe just make these getters and populate immediately after parsing
    //       instead of doing it in seperate `getParsed` methods.
    static final Cache<String, Town> towns = buildEmpty();
    static final Cache<String, Nation> nations = buildEmpty();
    static final Cache<String, Resident> residents = buildEmpty();
    static final Cache<String, Player> players = buildEmpty();

    public static void parsePlayerData() {
        JsonArray data = SquaremapAPI.playerData().getAsJsonArray("players");
        if (data.isEmpty()) return;

        rawPlayers.invalidateAll();

        arrAsStream(data).forEach(p -> {
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

                // This is not the player's Y level, but their Y head rot.
                obj.addProperty("yaw", keyAsInt(curPlayer, "yaw"));

                return obj;
            });
        });
    }

    public static void parseMapData(Boolean parseTowns, Boolean parseNations, Boolean parseResidents) {
        JsonArray data = SquaremapAPI.mapData();
        if (data.isEmpty()) return;

        // Remove all the data before computeIfAbsent runs - this ensures old data isn't kept.
        if (parseTowns) rawTowns.invalidateAll();
        if (parseNations) rawNations.invalidateAll();
        if (parseResidents) rawResidents.invalidateAll();

        //processMapData(data, parseTowns, parseNations, parseResidents);
    }

    public static void parseMapData() {
        parseMapData(true, true, true);
    }

    public static void processMapData(@NotNull JsonObject mapData,
        Boolean parseTowns, Boolean parseNations, Boolean parseResidents
    ) {
        streamValues(mapData.asMap()).forEach(cur -> {
            ProcessedMarker marker = new ProcessedMarker(cur.getAsJsonObject());

//            if (parseTowns) {
//                parseTowns(name, nation, mayorStr, wikiStr, residentNames, x, z, area, capital, info, fill, outline);
//            }
//
//            if (parseNations && nation != null) {
//                parseNations(nation, name, residentNames, mayorStr, area, x, z, capital);
//            }
//
//            if (parseResidents) {
//                parseResidents(members, name, nation, mayorStr, capital);
//            }
        });
    }

    private static void parseResidents() {

    }

    private static void parseTowns() {

    }

    private static void parseNations() {

    }

    public static Cache<String, Town> getParsedTowns() {
        return null;
    }

    public static Cache<String, Nation> getParsedNations() {
        return null;
    }

    public static Cache<String, Resident> getParsedResidents() {
        streamEntries(rawResidents.asMap()).forEach(entry ->
            residents.put(entry.getKey(), new Resident(entry.getValue()))
        );

        return residents;
    }

    public static Cache<String, Player> getParsedPlayers() {
        var residentMap = residents.asMap();

        streamEntries(rawPlayers.asMap()).forEach(entry -> {
            String key = entry.getKey();
            Player pl = new Player(entry.getValue(), residentMap.containsKey(key));

            players.put(key, pl);
        });

        return players;
    }
}