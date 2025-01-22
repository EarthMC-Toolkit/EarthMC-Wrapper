package io.github.emcw.squaremap;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.emcw.squaremap.entities.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import com.github.benmanes.caffeine.cache.Cache;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Objects;

import static io.github.emcw.utils.GsonUtil.*;

// TODO: Remove annotation when the class is fully complete.
@SuppressWarnings("unused")
public class SquaremapParser {
    // TODO: Maybe just make these getters and populate immediately after parsing
    //       instead of doing it in seperate `getParsed` methods.
    @Getter final Cache<String, SquaremapTown> towns = Caffeine.newBuilder().build();
    @Getter final Cache<String, SquaremapNation> nations = Caffeine.newBuilder().build();
    @Getter final Cache<String, SquaremapResident> residents = Caffeine.newBuilder().build();
    @Getter final Cache<String, SquaremapPlayer> players = Caffeine.newBuilder().build();

    public void parsePlayerData() {
        JsonArray data = SquaremapAPI.playerData().getAsJsonArray("players");
        if (data.isEmpty()) return;

        players.invalidateAll();

        arrAsStream(data).forEach(p -> {
            JsonObject curPlayer = p.getAsJsonObject();
            String name = keyAsStr(curPlayer, "name");
            String uuid = keyAsStr(curPlayer, "uuid");

            var playerMap = players.asMap();

            players.asMap().computeIfAbsent(name, k -> {
                JsonObject obj = new JsonObject();

                obj.addProperty("uuid", uuid);
                obj.addProperty("name", name);
                obj.addProperty("nickname", keyAsStr(curPlayer, "display_name"));
                obj.addProperty("world", keyAsStr(curPlayer, "world"));
                obj.addProperty("x", keyAsInt(curPlayer, "x"));
                obj.addProperty("z", keyAsInt(curPlayer, "z"));

                // This is not the player's Y level, but their Y head rot.
                obj.addProperty("yaw", keyAsInt(curPlayer, "yaw"));

                return new SquaremapPlayer(obj, playerMap.containsKey(uuid));
            });
        });
    }

    public void parseMapData(Boolean parseTowns, Boolean parseNations, Boolean parseResidents) {
        JsonArray data = SquaremapAPI.mapData();
        if (data.isEmpty()) return;

        // Remove all old data before computeIfAbsent runs.
        if (parseTowns) towns.invalidateAll();
        if (parseNations) nations.invalidateAll();
        if (parseResidents) residents.invalidateAll();

        processMapData(data, parseTowns, parseNations, parseResidents);
    }

    public void parseMapData() {
        parseMapData(true, true, true);
    }

    public void processMapData(@NotNull JsonArray mapData,
        Boolean parseTowns, Boolean parseNations, Boolean parseResidents
    ) {
        arrAsStream(mapData).forEach(markerEl -> {
            String type = keyAsStr(markerEl.getAsJsonObject(), "type");
            if (Objects.equals(type, "icon")) return;

            SquaremapMarker marker = new SquaremapMarker(markerEl.getAsJsonObject());

            // TODO: TEMPORARY TESTING - REMOVE WHEN FINISHED.
            if (Objects.equals(marker.nationName, "Poland") && marker.isCapital) {
                System.out.println(serialize(marker));
            }

            if (parseTowns) {
                parseTown(marker);
            }

            if (parseNations && marker.nationName != null) {
                parseNation(marker);
            }

            if (parseResidents) {
                parseResidents(marker);
            }
        });
    }

    void parseResidents(SquaremapMarker marker) {
        //members, name, nation, mayorStr, capital

    }

    void parseTown(SquaremapMarker marker) {
        //name, nation, mayorStr, wikiStr, residentNames, x, z, area, capital, info, fill, outline

        towns.asMap().computeIfAbsent(marker.townName, k -> {
//            JsonObject obj = new JsonObject();
//
//            //#region Add properties
//            obj.addProperty("townName", marker.townName);
//            obj.addProperty("nationName", marker.nationName);
//            obj.addProperty("mayor", marker.mayor);
//            obj.addProperty("wiki", marker.townWiki);
//            obj.add("residents", marker.residents);
//            obj.addProperty("x", range(x));
//            obj.addProperty("z", range(z));
//            obj.addProperty("area", area);
//
//            // Flags (3-8)
//            obj.addProperty("pvp", flagAsBool(info, 3, "pvp: "));
//            obj.addProperty("mobs", flagAsBool(info, 4, "mobs: "));
//            obj.addProperty("public", flagAsBool(info, 5, "public: "));
//            obj.addProperty("explosions", flagAsBool(info, 6, "pvp: "));
//            obj.addProperty("fire", flagAsBool(info, 7, "fire: "));
//            obj.addProperty("capital", capital);
//
//            obj.addProperty("fill", fill);
//            obj.addProperty("outline", outline);
//            //#endregion

            return new SquaremapTown(marker);
        });
    }

    void parseNation(SquaremapMarker marker) {
        //nation, name, residentNames, mayorStr, area, x, z, capital

//        // Not present, create a new Nation.
//        nations.asMap().computeIfAbsent(marker.nationName, k -> {
//            JsonObject obj = new JsonObject();
//            obj.addProperty("name", marker.nationName);
//
//            // Set default property values to be added to.
//            obj.add("towns", new JsonArray());
//            obj.add("residents", new JsonArray());
//            obj.addProperty("area", 0);
//
//            return obj;
//        });
//
//        // Nation is present, add current town prop values.
//        nations.asMap().computeIfPresent(marker.nationName, (k, v) -> {
//            Integer prevArea = keyAsInt(v, "area");
//            if (prevArea != null) v.addProperty("area", prevArea + area);
//
//            v.getAsJsonArray("towns").add(town);
//            v.getAsJsonArray("residents").addAll(residents);
//
//            if (capital) {
//                v.addProperty("wiki", keyAsBool(v, "wiki"));
//                v.addProperty("king", mayor);
//
//                // TODO: Get X and Z of capital. Use home block if available, otherwise midrange.
//                JsonObject capitalObj = new JsonObject();
//                capitalObj.addProperty("name", marker.townName);
//                capitalObj.addProperty("x", );
//                capitalObj.addProperty("z", );
//
//                v.add("capital", capitalObj);
//
//
//            }
//
//            return v;
//        });
    }
}