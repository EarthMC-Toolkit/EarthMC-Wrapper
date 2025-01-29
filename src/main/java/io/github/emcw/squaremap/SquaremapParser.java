package io.github.emcw.squaremap;

import io.github.emcw.squaremap.entities.*;
import static io.github.emcw.utils.GsonUtil.*;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Cache;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.Set;

// TODO: Remove annotation when the class is fully complete.
@SuppressWarnings("unused")
public abstract class SquaremapParser {
    @Getter final Cache<String, SquaremapTown> towns = Caffeine.newBuilder().build();
    @Getter final Cache<String, SquaremapNation> nations = Caffeine.newBuilder().build();
    @Getter final Cache<String, SquaremapResident> residents = Caffeine.newBuilder().build();
    @Getter final Cache<String, SquaremapOnlinePlayer> onlinePlayers = Caffeine.newBuilder().build();

    public void parsePlayerData() {
        JsonArray opData = SquaremapAPI.playerData().getAsJsonArray("players");
        if (opData.isEmpty()) return;

        // Remove players that may no longer be online.
        onlinePlayers.invalidateAll();

        opData.forEach(op -> {
            JsonObject opObj = op.getAsJsonObject();

            String name = keyAsStr(opObj, "name");
            if (name == null || name.isEmpty()) return; // This player obj is somehow broken, skip it.

            JsonObject opInfo = new JsonObject();
            opInfo.addProperty("uuid", keyAsStr(opObj, "uuid"));
            opInfo.addProperty("name", name);
            opInfo.addProperty("displayName", keyAsStr(opObj, "display_name"));
            opInfo.addProperty("world", keyAsStr(opObj, "world"));
            opInfo.addProperty("x", keyAsInt(opObj, "x"));
            opInfo.addProperty("z", keyAsInt(opObj, "z"));

            // This is not the player's Y level, but their Y head rot.
            opInfo.addProperty("yaw", keyAsInt(opObj, "yaw"));

            // We don't use UUID as the key since it's uncertain if it will always exist.
            // It's also easier for clients to search for names directly, i.e. `Players.get("Owen3H")`.
            onlinePlayers.asMap().put(name, new SquaremapOnlinePlayer(opInfo));
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

    public void processMapData(@NotNull JsonArray mapData, Boolean parseTowns, Boolean parseNations, Boolean parseResidents) {
        arrAsStream(mapData).forEach(markerEl -> {
            JsonObject markerObj = markerEl.getAsJsonObject();

            String type = keyAsStr(markerObj, "type");
            if (Objects.equals(type, "icon")) return;

            SquaremapMarker marker = new SquaremapMarker(markerObj);
            // TODO: Maybe return here if townName is null?

            // TODO: TEMPORARY TESTING - REMOVE WHEN FINISHED.
//            if (Objects.equals(marker.nationName, "Poland") && marker.isCapital) {
//                System.out.println(serialize(marker));
//            }

            Set<String> residentNames = marker.getResidentNames();
            Set<String> councillorNames = marker.getCouncillorNames();

            if (parseTowns) {
                parseTown(marker);
            }

            if (parseNations) {
                parseNation(marker, residentNames, councillorNames);
            }

            if (parseResidents && residentNames != null) {
                parseResidents(marker, residentNames, councillorNames);
            }
        });
    }

    void parseResidents(SquaremapMarker marker, Set<String> residentNames, Set<String> councillorNames) {
        residentNames.forEach(resName ->
            residents.asMap().putIfAbsent(resName, new SquaremapResident(resName, marker, councillorNames.contains(resName)))
        );
    }

    void parseTown(SquaremapMarker marker) {
        //name, nation, mayorStr, wikiStr, residentNames, x, z, area, capital, info, fill, outline
        if (marker.townName == null) return;
        towns.asMap().putIfAbsent(marker.townName, new SquaremapTown(marker));
    }

    void parseNation(SquaremapMarker marker, Set<String> residentNames, Set<String> councillorNames) {
        //nation, name, residentNames, mayorStr, area, x, z, capital
        if (marker.nationName == null) return;

        nations.asMap().compute(marker.nationName, (key, cachedNation) -> {
            // Try get existing cached nation, or create one if it doesn't exist.
            SquaremapNation nation = cachedNation != null ? cachedNation : new SquaremapNation(marker.nationName);
            nation.updateInfo(marker, residentNames, councillorNames); // Always merge marker data

            return nation;
        });
    }
}