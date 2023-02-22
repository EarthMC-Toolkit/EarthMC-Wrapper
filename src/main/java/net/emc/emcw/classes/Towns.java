package net.emc.emcw.classes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emcw.interfaces.Collective;
import net.emc.emcw.objects.Town;
import net.emc.emcw.utils.API;
import net.emc.emcw.utils.GsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import java.util.*;
import java.util.stream.Collectors;

import static net.emc.emcw.utils.GsonUtil.getGSON;
import static net.emc.emcw.utils.GsonUtil.keyAsStr;

public class Towns implements Collective<Town> {
    public Map<String, Town> cache = null;
    String map;

    public Towns(String mapName) {
        this.map = mapName;
        updateCache();
    }

    public Town single(String key) throws NullPointerException {
        return Collective.super.single(key, this.cache);
    }

    public List<Town> all() {
        return Collective.super.all(this.cache);
    }

    public void updateCache() {
        JsonObject towns = parsedTowns();

        // Convert to Town objects and use as cache.
        this.cache = toMap(towns);
    }

    public JsonObject parsedTowns() {
        Map<String, JsonElement> mapData = API.mapData(this.map);
        JsonObject all = new JsonObject();

        var areas = mapData.values();
        if (areas.size() < 1) return null;

        for (JsonElement town : areas) {
            JsonObject cur = town.getAsJsonObject();
            String desc = keyAsStr(cur, "desc");
            if (desc == null) continue;

            Safelist whitelist = new Safelist().addAttributes("a", "href");
            List<String> raw = Arrays.stream(desc.split("<br />"))
                    .map(e -> Jsoup.clean(e, whitelist))
                    .collect(Collectors.toList());

            String title = raw.get(0);
            if (raw.get(0).contains("(Shop)")) continue;
            raw.remove("Flags");

            Element link = Jsoup.parse(title).select("a").first();

            String nation = link != null ? link.text() : StringUtils.substringBetween(title, "(", ")");
            String wiki = link != null ? link.attr("href") : null;

            String name = keyAsStr(cur, "label");

            JsonObject obj = new JsonObject();
            obj.addProperty("name", name);

            if (Objects.equals(nation, ""))
                nation = "No Nation";

            obj.addProperty("nation", nation);
            obj.addProperty("wiki", wiki);

            assert name != null;
            all.add(name, getGSON().toJsonTree(obj));
        }

        return all;
    }

    public static Map<String, Town> toMap(JsonObject towns) {
        var itr = towns.entrySet().iterator();
        Map<String, Town> map = new HashMap<>();

        while (itr.hasNext()) {
            Map.Entry<String, JsonElement> next = null;
            try { next = itr.next(); }
            catch (NoSuchElementException e) {
                continue;
            }

            map.put(next.getKey(), new Town(next.getValue().getAsJsonObject()));
        }

        return map;
    }
}