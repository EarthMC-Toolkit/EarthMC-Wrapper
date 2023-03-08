package io.github.emcw.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.emcw.objects.Nation;
import io.github.emcw.objects.Player;
import io.github.emcw.objects.Town;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.emcw.utils.Funcs.collectAsMap;
import static io.github.emcw.utils.Funcs.streamEntries;
import static io.github.emcw.utils.GsonUtil.*;

public class DataParser {
    private static final JsonArray residents = new JsonArray();

    static ConcurrentHashMap<String, JsonObject>
            towns   = new ConcurrentHashMap<>(),
            nations = new ConcurrentHashMap<>(),
            players = new ConcurrentHashMap<>();

    static Safelist whitelist = new Safelist().addAttributes("a", "href");

    static List<String> processFlags(String str) {
        return Stream.of(str.split("<br />")).parallel()
                .map(e -> Jsoup.clean(e, whitelist))
                .collect(Collectors.toList());
    }

    public static void parsePlayerData(String map) {
        JsonArray pData = API.playerData(map).getAsJsonArray("players");

        arrAsStream(pData).forEach(p -> {
            JsonObject curPlayer = p.getAsJsonObject();
            String name = keyAsStr(curPlayer, "account");

            players.computeIfAbsent(name, k -> {
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

    public static void parseMapData(String map, Boolean parseNations) {
        Map<String, JsonElement> mapData = API.mapData(map).asMap();
        Collection<JsonElement> areas = mapData.values();
        if (areas.size() < 1) return;

        areas.parallelStream().forEach(town -> {
            JsonObject cur = town.getAsJsonObject();

            //#region Get and process keys (label, desc)
            String name = keyAsStr(cur, "label");
            if (name == null) return;

            String desc = keyAsStr(cur, "desc");
            if (desc == null) return;

            List<String> info = processFlags(desc);
            String title = info.get(0);

            if (title.contains("(Shop)")) return;
            info.remove("Flags");

            //System.out.println(info);
            //#endregion

            //#region Parse members flag & add to residents
            String names = StringUtils.substringBetween(String.join(", ", info), "Members ", ", pvp");
            if (names == null) return;

            String[] members = names.split(", ");
            JsonArray residentNames = arrFromStrArr(members);
            residents.addAll(residentNames);
            //#endregion

            //#region Variables from info (nation, wiki, mayor)
            Element link = Jsoup.parse(title).select("a").first();
            String nationStr = link != null ? link.text() : StringUtils.substringBetween(title, "(", ")");
            JsonElement nation = Objects.equals(nationStr, "") ? null : deserialize(nationStr, JsonElement.class);

            String wiki = link != null ? link.attr("href") : null;
            String mayor = info.get(1).replace("Mayor ", "");

            JsonArray x = keyAsArr(cur, "x");
            JsonArray z = keyAsArr(cur, "z");
            //#endregion

            //#region Create/Update Towns Map.
            towns.computeIfAbsent(name, k -> {
                JsonObject obj = new JsonObject();

                obj.addProperty("name", name);
                obj.addProperty("mayor", mayor);
                obj.addProperty("wiki", wiki);
                obj.add("nation", nation);
                obj.add("residents", residentNames);

                // Coord arrays
                obj.add("x", x);
                obj.add("z", z);

                // area
                Integer area = Funcs.calcArea(arrToIntArr(x), arrToIntArr(z), x.size(), 256);
                obj.addProperty("area", area);

                // flags

                return obj;
            });
            //#endregion

            //#region Create/Update Nations Map.
            if (!parseNations) return;
            if (nation != null) {
                String nationName = nation.getAsString();

                nations.computeIfPresent(nationName, (k, v) -> {
                    v.getAsJsonArray("towns").add(name);

                    return v;
                });

                nations.computeIfAbsent(nationName, k -> {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("name", nationName);

                    JsonArray townArr = new JsonArray();
                    townArr.add(name);
                    obj.add("towns", townArr);

                    JsonArray residentArr = new JsonArray();
                    residentArr.add(towns.get(name).get("residents"));
                    obj.add("residents", residentArr);

                    return obj;
                });
            }
            //#endregion
        });
    }

    public static Map<String, Player> playersAsMap(JsonObject players) {
        return collectAsMap(streamEntries(players).map(entry -> {
            try { return Map.entry(entry.getKey(), new Player(valueAsObj(entry))); }
            catch (Exception e) { return null; }
        }));
    }

    public static Map<String, Town> townsAsMap(JsonObject towns) {
        return collectAsMap(streamEntries(towns).map(entry -> {
            try { return Map.entry(entry.getKey(), new Town(valueAsObj(entry))); }
            catch (Exception e) { return null; }
        }));
    }

    public static Map<String, Nation> nationsAsMap(JsonObject nations) {
        return collectAsMap(streamEntries(nations).map(entry -> {
            try { return Map.entry(entry.getKey(), new Nation(valueAsObj(entry))); }
            catch (Exception e) { return null; }
        }));
    }

    public static JsonObject getTowns() {
        return toObj(towns);
    }

    public static JsonObject getNations() {
        return toObj(nations);
    }

    public static JsonObject getPlayers() {
        return toObj(players);
    }
}
