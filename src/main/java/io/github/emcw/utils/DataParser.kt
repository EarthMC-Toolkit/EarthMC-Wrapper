package io.github.emcw.utils

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.emcw.map.entities.Nation
import io.github.emcw.map.entities.Player
import io.github.emcw.map.entities.Resident
import io.github.emcw.map.entities.Town
import io.github.emcw.utils.http.DynmapAPI
import lombok.AccessLevel
import lombok.NoArgsConstructor
import org.apache.commons.lang3.StringUtils
import org.jetbrains.annotations.Contract
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import java.util.stream.Collectors

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object DataParser {
    private val whitelist = Safelist().addAttributes("a", "href")

    private val rawTowns = buildEmpty<String, JsonObject>()
    private val rawNations = buildEmpty<String, JsonObject>()
    private val rawResidents = buildEmpty<String, JsonObject>()
    private val rawPlayers = buildEmpty<String?, JsonObject>()

    private val towns = buildEmpty<String, Town>()
    private val nations = buildEmpty<String, Nation>()
    private val residents = buildEmpty<String, Resident>()
    private val playerCache = buildEmpty<String, Player>()

    @Contract(" -> new")
    fun <K, V> buildEmpty(): Cache<K, V> {
        return Caffeine.newBuilder().build()
    }

    private fun processFlags(str: String): MutableList<String> {
        return GsonUtil.strArrAsStream(str.split("<br />".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            .map { e: String -> Jsoup.clean(e, whitelist) }
            .collect(Collectors.toList())
    }

    private fun flagAsBool(info: List<String>, index: Int?, key: String?): Boolean {
        val str = info[index!!].replace(key!!, "")
        return java.lang.Boolean.parseBoolean(str)
    }

    @JvmStatic
    @JvmOverloads
    fun parseMapData(map: String?, parseTowns: Boolean = true, parseNations: Boolean = true, parseResidents: Boolean = true) {
        val mapData = DynmapAPI.mapData(map)
        if (mapData.size() < 1) return

        if (parseTowns) rawTowns.invalidateAll()
        if (parseNations) rawNations.invalidateAll()
        if (parseResidents) rawResidents.invalidateAll()

        processMapData(mapData, parseTowns, parseNations, parseResidents)
    }

    private fun processMapData(
        mapData: JsonObject,
        parseTowns: Boolean,
        parseNations: Boolean,
        parseResidents: Boolean
    ) {
        GsonUtil.streamValues(mapData.asMap()).forEach { town: JsonElement ->
            val cur = town.asJsonObject
            //ProcessedTown processed = new ProcessedTown(cur);

            //#region Get and process keys (label, desc)
            val name = GsonUtil.keyAsStr(cur, "label") ?: return@forEach
            val desc = GsonUtil.keyAsStr(cur, "desc") ?: return@forEach
            val info = processFlags(desc)
            val title = info[0]
            if (title.contains("(Shop)")) return@forEach
            info.remove("Flags")
            //#endregion

            //#region Parse members flag & add to rawResidents
            val names = StringUtils.substringBetween(
                    info.joinToString { ", " }, "Members ", ", pvp"
            ) ?: return@forEach

            val members = names.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val residentNames = GsonUtil.arrFromStrArr(members)
            //#endregion

            //#region Variables from info
            val link = Jsoup.parse(title).select("a").first()
            val nationStr = if (link != null) link.text() else StringUtils.substringBetween(title, "(", ")")
            val nation = if (nationStr == "") null else nationStr
            val wikiStr = link?.attr("href")
            val mayorStr = info[1].replace("Mayor ", "")

            val x = GsonUtil.arrToIntArr(GsonUtil.keyAsArr(cur, "x"))
            val z = GsonUtil.arrToIntArr(GsonUtil.keyAsArr(cur, "z"))
            val area = Funcs.calcArea(x, z)

            val fill = GsonUtil.keyAsStr(cur, "fillcolor")
            val outline = GsonUtil.keyAsStr(cur, "color")

            val capital = flagAsBool(info, 8, "capital: ")
            //#endregion

            if (parseTowns) parseTowns(name, nation, mayorStr, wikiStr, residentNames, x, z, area, capital, info, fill, outline)
            if (parseNations && nation != null) parseNations(nation, name, residentNames, mayorStr, area, x, z, capital)
            if (parseResidents) parseResidents(members, name, nation, mayorStr, capital)
        }
    }

    private fun parseTowns(
        name: String, nation: String?, mayor: String, wiki: String?,
        residents: JsonArray, x: IntArray, z: IntArray, area: Int, capital: Boolean,
        info: List<String>, fill: String?, outline: String?
    ) {
        rawTowns.asMap().computeIfAbsent(name) { _: String? ->
            val obj = JsonObject()

            //#region Add properties
            obj.addProperty("name", name)
            obj.addProperty("nation", nation)
            obj.addProperty("mayor", mayor)
            obj.addProperty("wiki", wiki)
            obj.add("residents", residents)
            obj.addProperty("x", Funcs.range(x))
            obj.addProperty("z", Funcs.range(z))
            obj.addProperty("area", area)

            // Flags (3-8)
            obj.addProperty("pvp", flagAsBool(info, 3, "pvp: "))
            obj.addProperty("mobs", flagAsBool(info, 4, "mobs: "))
            obj.addProperty("public", flagAsBool(info, 5, "public: "))
            obj.addProperty("explosions", flagAsBool(info, 6, "pvp: "))
            obj.addProperty("fire", flagAsBool(info, 7, "fire: "))
            obj.addProperty("capital", capital)
            obj.addProperty("fill", fill)
            obj.addProperty("outline", outline)
            obj
        }
    }

    private fun parseNations(
        nation: String, town: String, residents: JsonArray,
        mayor: String, area: Int, x: IntArray, z: IntArray, capital: Boolean
    ) {
        // Not present, create a new Nation.
        rawNations.asMap().computeIfAbsent(nation) { _: String? ->
            val obj = JsonObject()
            obj.addProperty("name", nation)

            // Set default property values to be added to.
            obj.add("towns", JsonArray())
            obj.add("residents", JsonArray())
            obj.addProperty("area", 0)
            obj
        }

        // Nation is present, add current town prop values.
        rawNations.asMap().computeIfPresent(nation) { _: String?, v: JsonObject ->
            val prevArea = GsonUtil.keyAsInt(v, "area")
            if (prevArea != null) v.addProperty("area", prevArea + area)

            v.getAsJsonArray("towns").add(town)
            v.getAsJsonArray("residents").addAll(residents)

            if (capital) {
                v.addProperty("wiki", GsonUtil.keyAsBool(v, "wiki"))
                v.addProperty("king", mayor)

                val capitalObj = JsonObject()
                capitalObj.addProperty("name", town)
                capitalObj.addProperty("x", Funcs.range(x))
                capitalObj.addProperty("z", Funcs.range(z))

                v.add("capital", capitalObj)
            }

            v
        }
    }

    private fun parseResidents(members: Array<String>, town: String, nation: String?, mayor: String, capital: Boolean) {
        GsonUtil.strArrAsStream(members).forEach { res: String ->
            val obj = JsonObject()
            obj.addProperty("name", res)
            obj.addProperty("town", town)
            obj.addProperty("nation", nation)

            val rank = if (mayor == res) (if (capital) "Nation Leader" else "Mayor") else "Resident"
            obj.addProperty("rank", rank)

            // Add resident to rawResidents.
            rawResidents.put(res, obj)
        }
    }

    @JvmStatic
    fun parsePlayerData(map: String?) {
        val pData = DynmapAPI.playerData(map).getAsJsonArray("players")
        if (pData.size() < 1) return

        rawPlayers.invalidateAll()

        GsonUtil.arrAsStream(pData).forEach { p: JsonElement ->
            val curPlayer = p.asJsonObject
            val name = GsonUtil.keyAsStr(curPlayer, "account")

            rawPlayers.asMap().computeIfAbsent(name) { k: String? ->
                val obj = JsonObject()
                obj.addProperty("name", name)
                obj.addProperty("nickname", GsonUtil.keyAsStr(curPlayer, "name"))
                obj.addProperty("world", GsonUtil.keyAsStr(curPlayer, "world"))
                obj.addProperty("x", GsonUtil.keyAsInt(curPlayer, "x"))
                obj.addProperty("y", GsonUtil.keyAsInt(curPlayer, "y"))
                obj.addProperty("z", GsonUtil.keyAsInt(curPlayer, "z"))
                obj
            }
        }
    }

    @JvmStatic
    fun parsedTowns(): Cache<String, Town> {
        GsonUtil.streamEntries(rawTowns.asMap()).forEach { entry: Map.Entry<String, JsonObject> -> towns.put(entry.key, Town(GsonUtil.valueAsObj(entry))) }
        return towns
    }

    @JvmStatic
    fun parsedNations(map: String?): Cache<String, Nation> {
        GsonUtil.streamEntries(rawNations.asMap()).forEach { entry: Map.Entry<String, JsonObject> -> nations.put(entry.key, Nation(GsonUtil.valueAsObj(entry), map)) }
        return nations
    }

    @JvmStatic
    fun parsedResidents(): Cache<String, Resident> {
        GsonUtil.streamEntries(rawResidents.asMap()).forEach { entry: Map.Entry<String, JsonObject> -> residents.put(entry.key, Resident(GsonUtil.valueAsObj(entry))) }
        return residents
    }

    @JvmStatic
    fun parsedPlayers(): Cache<String, Player> {
        GsonUtil.streamEntries(rawPlayers.asMap()).forEach { entry: Map.Entry<String, JsonObject> ->
            val key = entry.key
            val pl = Player(GsonUtil.valueAsObj(entry), residents.asMap().containsKey(key))
            playerCache.put(key, pl)
        }

        return playerCache
    }
}