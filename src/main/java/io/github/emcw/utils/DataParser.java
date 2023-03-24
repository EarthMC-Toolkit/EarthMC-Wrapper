package io.github.emcw.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.objects.Nation;
import io.github.emcw.objects.Player;
import io.github.emcw.objects.Resident;
import io.github.emcw.objects.Town;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.emcw.utils.Funcs.*;
import static io.github.emcw.utils.GsonUtil.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataParser {
    static Safelist whitelist = new Safelist().addAttributes("a", "href");

    @Getter static BaseCache<JsonObject> towns = new BaseCache<>();
    @Getter static BaseCache<JsonObject> nations = new BaseCache<>();
    @Getter static BaseCache<JsonObject> players = new BaseCache<>();
    @Getter static BaseCache<JsonObject> residents = new BaseCache<>();

    static List<String> processFlags(String str) {
        return strArrAsStream(str.split("<br />"))
                .map(e -> Jsoup.clean(e, whitelist))
                .collect(Collectors.toList());
    }

    public static void parsePlayerData(String map) {
        players.cache.invalidateAll();

        JsonArray pData = API.playerData(map).getAsJsonArray("players");
        if (pData.size() < 1) return;

        arrAsStream(pData).forEach(p -> {
            JsonObject curPlayer = p.getAsJsonObject();
            String name = keyAsStr(curPlayer, "account");

            players.all().computeIfAbsent(name, k -> {
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

    static boolean flagAsBool(@NotNull List<String> info, Integer index, String key) {
        String str = info.get(index).replace(key, "");
        return Boolean.parseBoolean(str);
    }

    public static void parseMapData(String map) {
        parseMapData(map, true, true, true);
    }

    public static void parseMapData(String map, Boolean parseTowns, Boolean parseNations, Boolean parseResidents) {
        towns.cache.invalidateAll();
        nations.cache.invalidateAll();
        residents.cache.invalidateAll();

        JsonObject mapData = API.mapData(map);
        if (mapData.size() < 1) return;

        streamValues(mapData.asMap()).forEach(town -> {
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

            Boolean capital = flagAsBool(info, 8, "capital: ");
            //#endregion

            //#region Create/Update Towns Map.
            if (parseTowns) {
                towns.all().computeIfAbsent(name, k -> {
                    JsonObject obj = new JsonObject();

                    //#region Add properties
                    obj.addProperty("name", name);
                    obj.addProperty("nation", nation);
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
                    obj.addProperty("capital", capital);

                    obj.addProperty("fill", fill);
                    obj.addProperty("outline", outline);
                    //#endregion

                    return obj;
                });
            }
            //#endregion

            //#region Create/Update Nations Map.
            if (parseNations && nation != null) {
                // Not present, create a new Nation.
                nations.all().computeIfAbsent(nation, k -> {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("name", nation);

                    // Set default property values to be added to.
                    obj.add("towns", new JsonArray());
                    obj.add("residents", new JsonArray());
                    obj.addProperty("area", 0);

                    return obj;
                });

                // Nation is present, add current town prop values.
                nations.all().computeIfPresent(nation, (k, v) -> {
                    v.getAsJsonArray("towns").add(name);
                    v.getAsJsonArray("residents").addAll(residentNames);
                    v.addProperty("area", v.get("area").getAsInt() + area);

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

            if (parseResidents) {
                // Loop through members
                strArrAsStream(members).forEach(res -> {
                    // Create new object (name, town, nation, rank)
                    JsonObject newObj = new JsonObject();

                    newObj.addProperty("name", res);
                    newObj.addProperty("town", name);
                    newObj.addProperty("nation", nation);

                    String rank = mayorStr.equals(res) ? (capital ? "Nation Leader" : "Mayor") : "Resident";
                    newObj.addProperty("rank", rank);

                    // Add resident obj to residents.
                    residents.cache.put(res, newObj);
                });
            }
            //#endregion
        });
    }

    public static Map<String, Town> parsedTowns() {
        return collectAsMap(streamEntries(towns.all()).map(entry -> {
            try { return Map.entry(entry.getKey(), new Town(valueAsObj(entry))); }
            catch (Exception e) { return null; }
        }));
    }

    public static Map<String, Nation> parsedNations() {
        return collectAsMap(streamEntries(nations.all()).map(entry -> {
            try { return Map.entry(entry.getKey(), new Nation(valueAsObj(entry))); }
            catch (Exception e) { return null; }
        }));
    }

    public static Map<String, Resident> parsedResidents() {
        return collectAsMap(streamEntries(residents.all()).map(entry -> {
            try { return Map.entry(entry.getKey(), new Resident(valueAsObj(entry))); }
            catch (Exception e) { return null; }
        }));
    }

    public static Map<String, Player> parsedPlayers() {
        return collectAsMap(streamEntries(players.all()).map(entry -> {
            try { return Map.entry(entry.getKey(), new Player(valueAsObj(entry))); }
            catch (Exception e) { return null; }
        }));
    }
}
