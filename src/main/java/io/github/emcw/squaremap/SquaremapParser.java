package io.github.emcw.squaremap;

import io.github.emcw.squaremap.entities.*;
import static io.github.emcw.utils.GsonUtil.*;

import lombok.Getter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// TODO: Parser can be static if we get rid of caches and just provide
//       parse methods that return what the getters would anyway.
@SuppressWarnings({"unused", "LombokGetterMayBeUsed"})
public class SquaremapParser {
    // Last write timestamp, data object.
    private JsonArray mapData = null;
    private Long mapDataLastWrite = null;

    @Getter final Map<String, SquaremapTown> towns = new HashMap<>();
    @Getter final Map<String, SquaremapNation> nations = new HashMap<>();
    @Getter final Map<String, SquaremapResident> residents = new HashMap<>();
    @Getter final Map<String, SquaremapOnlinePlayer> onlinePlayers = new HashMap<>();

    private final String mapName;

    public SquaremapParser(@NotNull String mapName) {
        this.mapName = mapName;
    }

    public void parsePlayerData() {
        JsonArray ops = SquaremapAPI.playerData(this.mapName).getAsJsonArray("players");
        if (ops.isEmpty()) return;

        // Remove players that may no longer be online.
        onlinePlayers.clear();

        ops.forEach(op -> {
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
            onlinePlayers.put(name, new SquaremapOnlinePlayer(opInfo));
        });
    }

    public void parseMapData(Boolean parseTowns, Boolean parseNations, Boolean parseResidents) throws Exception {
        JsonArray data = getOrFetchMapData();

        // Remove all old entries so caches will always contain fresh data.
        if (parseTowns) towns.clear();
        if (parseNations) nations.clear();
        if (parseResidents) residents.clear();

        //#region Process the marker and use it to parse town, nation and residents in same pass.
        data.forEach(markerEl -> {
            JsonObject markerObj =  markerEl.getAsJsonObject();

            String type = keyAsStr(markerObj, "type");
            if (Objects.equals(type, "icon")) return;

            // Takes HTML from the popup and tooltip of the raw marker and
            // uses JSoup to turn it into very basic 'intermediate' data.
            SquaremapMarker marker = new SquaremapMarker(markerObj);

            // TODO: Maybe return here if `marker.townName` is null?

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
        //#endregion
    }

    void parseResidents(SquaremapMarker marker, Set<String> residentNames, Set<String> councillorNames) {
        residentNames.forEach(resName ->
            residents.putIfAbsent(resName, new SquaremapResident(resName, marker, councillorNames.contains(resName)))
        );
    }

    void parseTown(SquaremapMarker marker) {
        if (marker.townName == null) return;
        towns.putIfAbsent(marker.townName, new SquaremapTown(marker));
    }

    void parseNation(SquaremapMarker marker, Set<String> residentNames, Set<String> councillorNames) {
        if (marker.nationName == null) return;

        nations.compute(marker.nationName, (key, cachedNation) -> {
            // Try get existing cached nation, or create one if it doesn't exist.
            SquaremapNation nation = cachedNation != null ? cachedNation : new SquaremapNation(marker.nationName);
            nation.updateInfo(marker, residentNames, councillorNames); // Always merge marker data

            return nation;
        });
    }

    public synchronized JsonArray getOrFetchMapData() throws Exception {
        // No map data stored already or it became stale (>5s since last write).
        if (mapData == null || isMapDataStale(5)) {
            mapData = SquaremapAPI.mapData(this.mapName);
            if (mapData.isEmpty()) {
                throw new Exception("Cannot parse map data! Received empty array.");
            }

            // Update with fresh data and update last write time
            mapDataLastWrite = System.currentTimeMillis();

            //System.out.println("Data fetched, last write is now: " + mapDataLastWrite);
        }

        return mapData;
    }

    @SuppressWarnings("SameParameterValue")
    boolean isMapDataStale(int secUntilStale) {
        if (mapDataLastWrite == null) {
            return true; // No timestamp exists, consider it stale.
        }

        long elapsed = System.currentTimeMillis() - mapDataLastWrite;
        return elapsed > secUntilStale * 1000L;
    }
}