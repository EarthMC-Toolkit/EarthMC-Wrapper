package io.github.emcw.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.emcw.entities.Nation;
import io.github.emcw.entities.Player;
import io.github.emcw.entities.Resident;
import io.github.emcw.entities.Town;

import io.github.emcw.utils.http.DynmapAPI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.github.emcw.utils.Funcs.*;
import static io.github.emcw.utils.GsonUtil.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataParser {
    static final Safelist whitelist = new Safelist().addAttributes("a", "href");

    static final Cache<String, JsonObject> rawTowns = buildEmpty();
    static final Cache<String, JsonObject> rawNations = buildEmpty();
    static final Cache<String, JsonObject> rawResidents = buildEmpty();
    static final Cache<String, JsonObject> rawPlayers = buildEmpty();

    private static final Cache<String, Town> towns = buildEmpty();
    private static final Cache<String, Nation> nations = buildEmpty();
    private static final Cache<String, Resident> residents = buildEmpty();
    private static final Cache<String, Player> playerCache = buildEmpty();

    @Contract(" -> new")
    static <K, V> @NotNull Cache<K, V> buildEmpty() {
        return Caffeine.newBuilder().build();
    }

    static List<String> processFlags(@NotNull String str) {
        return strArrAsStream(str.split("<br />"))
                .map(e -> Jsoup.clean(e, whitelist))
                .collect(Collectors.toList());
    }

    static boolean flagAsBool(@NotNull List<String> info, Integer index, String key) {
        String str = info.get(index).replace(key, "");
        return Boolean.parseBoolean(str);
    }

    public static void parseMapData(String map) {
        parseMapData(map, true, true, true);
    }

    public static void parseMapData(String map, Boolean parseTowns, Boolean parseNations, Boolean parseResidents) {
        JsonObject mapData = DynmapAPI.mapData(map);
        if (mapData.size() < 1) return;

        if (parseTowns) rawTowns.invalidateAll();
        if (parseNations) rawNations.invalidateAll();
        if (parseResidents) rawResidents.invalidateAll();

        processMapData(mapData, parseTowns, parseNations, parseResidents);
    }

    private static void processMapData(
        @NotNull JsonObject mapData,
        Boolean parseTowns,
        Boolean parseNations,
        Boolean parseResidents
    ) {
        streamValues(mapData.asMap()).forEach(town -> {
            JsonObject cur = town.getAsJsonObject();
            //ProcessedTown processed = new ProcessedTown(cur);

            //#region Get and process keys (label, desc)
            String name = keyAsStr(cur, "label");
            if (name == null) return;

            String desc = keyAsStr(cur, "desc");
            if (desc == null) return;

            List<String> info = processFlags(desc);
            String title = info.get(0);

            if (title.contains("(Shop)")) return;
            info.remove("Flags");
            //#endregion

            //#region Parse members flag & add to rawResidents
            String names = StringUtils.substringBetween(String.join(", ", info), "Members ", ", pvp");
            if (names == null) return;

            String[] members = names.split(", ");
            JsonArray residentNames = arrFromStrArr(members);
            //#endregion

            //#region Variables from info
            Element link = Jsoup.parse(title).select("a").first();
            String nationStr = link != null ? link.text() : StringUtils.substringBetween(title, "(", ")");
            String nation = Objects.equals(nationStr, "") ? null : nationStr;

            String wikiStr = link != null ? link.attr("href") : null;
            String mayorStr = info.get(1).replace("Mayor ", "");

            int[] x = arrToIntArr(keyAsArr(cur, "x"));
            int[] z = arrToIntArr(keyAsArr(cur, "z"));
            int area = calcArea(x, z);

            String fill = keyAsStr(cur, "fillcolor");
            String outline = keyAsStr(cur, "color");

            boolean capital = flagAsBool(info, 8, "capital: ");
            //#endregion

            if (parseTowns)
                parseTowns(name, nation, mayorStr, wikiStr, residentNames, x, z, area, capital, info, fill, outline);

            if (parseNations && nation != null)
                parseNations(nation, name, residentNames, mayorStr, area, x, z, capital);

            if (parseResidents)
                parseResidents(members, name, nation, mayorStr, capital);
        });
    }

    private static void parseTowns(
        String name, String nation, String mayor, String wiki,
        JsonArray residents, int[] x, int[] z, int area, Boolean capital,
        List<String> info, String fill, String outline
    ) {
        rawTowns.asMap().computeIfAbsent(name, k -> {
            JsonObject obj = new JsonObject();

            //#region Add properties
            obj.addProperty("name", name);
            obj.addProperty("nation", nation);
            obj.addProperty("mayor", mayor);
            obj.addProperty("wiki", wiki);
            obj.add("residents", residents);
            obj.addProperty("x", range(x));
            obj.addProperty("z", range(z));
            obj.addProperty("area", area);

            // Flags (3-8)
            obj.addProperty("pvp", flagAsBool(info, 3, "pvp: "));
            obj.addProperty("mobs", flagAsBool(info, 4, "mobs: "));
            obj.addProperty("public", flagAsBool(info, 5, "public: "));
            obj.addProperty("explosions", flagAsBool(info, 6, "pvp: "));
            obj.addProperty("fire", flagAsBool(info, 7, "fire: "));
            obj.addProperty("capital", capital);

            obj.addProperty("fill", fill);
            obj.addProperty("outline", outline);
            //#endregion

            return obj;
        });
    }

    private static void parseNations(
        String nation, String town, JsonArray residents,
        String mayor, int area, int[] x, int[] z, boolean capital
    ) {
        // Not present, create a new Nation.
        rawNations.asMap().computeIfAbsent(nation, k -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", nation);

            // Set default property values to be added to.
            obj.add("towns", new JsonArray());
            obj.add("residents", new JsonArray());
            obj.addProperty("area", 0);

            return obj;
        });

        // Nation is present, add current town prop values.
        rawNations.asMap().computeIfPresent(nation, (k, v) -> {
            Integer prevArea = keyAsInt(v, "area");
            if (prevArea != null) v.addProperty("area", prevArea + area);

            v.getAsJsonArray("towns").add(town);
            v.getAsJsonArray("residents").addAll(residents);

            if (capital) {
                v.addProperty("wiki", keyAsBool(v, "wiki"));
                v.addProperty("king", mayor);

                JsonObject capitalObj = new JsonObject();
                capitalObj.addProperty("name", town);
                capitalObj.addProperty("x", range(x));
                capitalObj.addProperty("z", range(z));

                v.add("capital", capitalObj);
            }

            return v;
        });
    }

    private static void parseResidents(String[] members, String town, String nation, String mayor, Boolean capital) {
        // Loop through members
        strArrAsStream(members).forEach(res -> {
            // Create new object (name, town, nation, rank)
            JsonObject obj = new JsonObject();

            obj.addProperty("name", res);
            obj.addProperty("town", town);
            obj.addProperty("nation", nation);

            String rank = mayor.equals(res) ? (capital ? "Nation Leader" : "Mayor") : "Resident";
            obj.addProperty("rank", rank);

            // Add resident to rawResidents.
            rawResidents.put(res, obj);
        });
    }

    public static void parsePlayerData(String map) {
        JsonArray pData = DynmapAPI.playerData(map).getAsJsonArray("players");
        if (pData.size() < 1) return;

        rawPlayers.invalidateAll();

        arrAsStream(pData).forEach(p -> {
            JsonObject curPlayer = p.getAsJsonObject();
            String name = keyAsStr(curPlayer, "account");

            rawPlayers.asMap().computeIfAbsent(name, k -> {
                JsonObject obj = new JsonObject();

                obj.addProperty("name", name);
                obj.addProperty("nickname", keyAsStr(curPlayer, "name"));
                obj.addProperty("world", keyAsStr(curPlayer, "world"));
                obj.addProperty("x", keyAsInt(curPlayer, "x"));
                obj.addProperty("y", keyAsInt(curPlayer, "y"));
                obj.addProperty("z", keyAsInt(curPlayer, "z"));

                return obj;
            });
        });
    }

    public static Cache<String, Town> parsedTowns() {
        streamEntries(rawTowns.asMap()).forEach(entry ->
            towns.put(entry.getKey(), new Town(valueAsObj(entry)))
        );

        return towns;
    }

    public static Cache<String, Nation> parsedNations(String map) {
        streamEntries(rawNations.asMap()).forEach(entry ->
            nations.put(entry.getKey(), new Nation(valueAsObj(entry), map))
        );

        return nations;
    }

    public static Cache<String, Resident> parsedResidents() {
        streamEntries(rawResidents.asMap()).forEach(entry ->
            residents.put(entry.getKey(), new Resident(valueAsObj(entry)))
        );

        return residents;
    }

    public static Cache<String, Player> parsedPlayers() {
        streamEntries(rawPlayers.asMap()).forEach(entry -> {
            String key = entry.getKey();
            Player pl = new Player(valueAsObj(entry), residents.asMap().containsKey(key));

            playerCache.put(key, pl);
        });

        return playerCache;
    }
}