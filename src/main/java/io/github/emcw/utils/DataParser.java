package io.github.emcw.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.emcw.objects.Nation;
import io.github.emcw.objects.Player;
import io.github.emcw.objects.Resident;
import io.github.emcw.objects.Town;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.emcw.utils.Funcs.*;
import static io.github.emcw.utils.GsonUtil.*;

public class DataParser {
    private static final JsonArray allResidents = new JsonArray();

    static ConcurrentHashMap<String, JsonObject>
            towns   = new ConcurrentHashMap<>(),
            nations = new ConcurrentHashMap<>(),
            players = new ConcurrentHashMap<>(),
            residents = new ConcurrentHashMap<>();

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

    static boolean flagAsBool(List<String> info, Integer index, String key) {
        String str = info.get(index).replace(key, "");
        return Boolean.parseBoolean(str);
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

            //System.out.println(serialize(info));
            //#endregion

            //#region Parse members flag & add to residents
            String names = StringUtils.substringBetween(String.join(", ", info), "Members ", ", pvp");
            if (names == null) return;

            String[] members = names.split(", ");
            JsonArray residentNames = arrFromStrArr(members);
            allResidents.addAll(residentNames);
            //#endregion

            //#region Variables from info
            Element link = Jsoup.parse(title).select("a").first();
            String nationStr = link != null ? link.text() : StringUtils.substringBetween(title, "(", ")");
            JsonElement nation = Objects.equals(nationStr, "") ? null : deserialize(nationStr, JsonElement.class);

            String wikiStr = link != null ? link.attr("href") : null;
            String mayorStr = info.get(1).replace("Mayor ", "");

            int[] x = arrToIntArr(keyAsArr(cur, "x"));
            int[] z = arrToIntArr(keyAsArr(cur, "z"));
            int area = calcArea(x, z);

            String fill = keyAsStr(cur, "fillcolor");
            String outline = keyAsStr(cur, "color");
            //#endregion

            //#region Create/Update Towns Map.
            towns.computeIfAbsent(name, k -> {
                JsonObject obj = new JsonObject();

                //#region Add properties
                obj.addProperty("name", name);
                obj.add("nation", nation);
                obj.addProperty("mayor", mayorStr);
                obj.addProperty("wiki", wikiStr);
                obj.add("residents", residentNames);
                obj.addProperty("x", range(x));
                obj.addProperty("z", range(z));
                obj.addProperty("area", area);

                // Flags (3-8)
                obj.addProperty("pvp", flagAsBool(info, 3, "pvp: "));
                obj.addProperty("mobs", flagAsBool(info, 4, "mobs: "));
                obj.addProperty("public", flagAsBool(info, 5, "public: "));
                obj.addProperty("explosions", flagAsBool(info, 6, "pvp: "));
                obj.addProperty("fire", flagAsBool(info, 7, "fire: "));
                obj.addProperty("capital", flagAsBool(info, 8, "capital: "));

                obj.addProperty("fill", fill);
                obj.addProperty("outline", outline);
                //#endregion

                return obj;
            });
            //#endregion

            //#region Create/Update Nations Map.
            if (!parseNations) return;
            if (nation != null) {
                String nationName = nation.getAsString();

                // Not present, create a new Nation.
                nations.computeIfAbsent(nationName, k -> {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("name", nationName);

                    // Set default property values to be added to.
                    obj.add("towns", new JsonArray());
                    obj.add("residents", new JsonArray());
                    obj.addProperty("area", 0);

                    return obj;
                });

                // Nation is present, add current town prop values.
                nations.computeIfPresent(nationName, (k, v) -> {
                    v.getAsJsonArray("towns").add(name);
                    v.getAsJsonArray("residents").addAll(residentNames);
                    v.addProperty("area", v.get("area").getAsInt()+area);

                    boolean capital = keyAsBool(towns.get(name), "capital");
                    if (capital) {
                        v.addProperty("wiki", keyAsBool(v, "wiki"));
                        v.addProperty("king", mayorStr);

                        JsonObject capitalObj = new JsonObject();
                        capitalObj.addProperty("name", name);
                        capitalObj.addProperty("x", range(x));
                        capitalObj.addProperty("z", range(z));

                        v.add("capital", capitalObj);
                    }

                    return v;
                });
            }
            //#endregion
        });
    }

    public static Map<String, Resident> residentsAsMap(JsonObject residents) {
        return collectAsMap(streamEntries(residents).map(entry -> {
            try { return Map.entry(entry.getKey(), new Resident(valueAsObj(entry))); }
            catch (Exception e) { return null; }
        }));
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
        return mapToObj(towns);
    }

    public static JsonObject getNations() {
        return mapToObj(nations);
    }

    public static JsonObject getPlayers() {
        return mapToObj(players);
    }

    public static JsonObject getResidents() {
        return mapToObj(residents);
    }
}
