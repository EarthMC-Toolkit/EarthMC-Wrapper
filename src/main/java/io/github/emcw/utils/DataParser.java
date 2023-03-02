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

import static io.github.emcw.utils.Generics.collectAsMap;
import static io.github.emcw.utils.Generics.streamEntries;
import static io.github.emcw.utils.GsonUtil.keyAsStr;
import static io.github.emcw.utils.GsonUtil.valueAsObj;

public class DataParser {
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
        JsonObject pData = API.playerData(map);
    }

    public static void parseMapData(String map, Boolean parseNations) {
        Map<String, JsonElement> mapData = API.mapData(map);
        Collection<JsonElement> areas = mapData.values();
        if (areas.size() < 1) return;

        areas.parallelStream().forEach(town -> {
            JsonObject cur = town.getAsJsonObject();

            String name = keyAsStr(cur, "label");
            if (name == null) return;

            String desc = keyAsStr(cur, "desc");
            if (desc == null) return;

            List<String> info = processFlags(desc);
            String title = info.get(0);

            if (title.contains("(Shop)")) return;
            info.remove("Flags");

            //System.out.println(info);

            Element link = Jsoup.parse(title).select("a").first();

            String nationStr = link != null ? link.text() : StringUtils.substringBetween(title, "(", ")");
            JsonElement nation = Objects.equals(nationStr, "")
                    ? null : GsonUtil.deserialize(nationStr, JsonElement.class);

            String wiki = link != null ? link.attr("href") : null;
            String mayor = info.get(1).replace("Mayor ", "");

            String names = StringUtils.substringBetween(String.join(", ", info), "Members ", ", pvp");
            String[] members = names.split(", ");

            towns.computeIfAbsent(name, k -> {
                JsonObject obj = new JsonObject();

                obj.addProperty("name", name);
                obj.addProperty("mayor", mayor);
                obj.addProperty("wiki", wiki);
                obj.add("nation", nation);
                obj.add("residents", GsonUtil.arrFromStrArr(members));

                return obj;
            });

            if (!parseNations) return;
            if (nation != null) {
                nations.computeIfPresent(nation.getAsString(), (k, v) -> {
                    v.getAsJsonArray("towns").add(towns.get(name));
                    return v;
                });

                nations.computeIfAbsent(nation.getAsString(), k -> {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("name", nation.getAsString());

                    JsonArray townArr = new JsonArray();
                    townArr.add(towns.get(name));
                    obj.add("towns", townArr);

                    return obj;
                });
            }
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

    public static JsonObject toObj(Map<String, JsonObject> map) {
        JsonObject obj = new JsonObject();
        map.forEach(obj::add);
        return obj;
    }
}
