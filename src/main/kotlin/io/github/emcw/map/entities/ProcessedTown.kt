package io.github.emcw.map.entities

import com.google.gson.JsonObject
import io.github.emcw.utils.Funcs
import io.github.emcw.utils.GsonUtil
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import java.util.stream.Collectors

@Suppress("unused")
class ProcessedTown(obj: JsonObject) {
    val name: String?
    val desc: String?
    val fill: String?
    val outline: String?
    val x: IntArray?
    val z: IntArray?
    val area: Int
    val info: List<String>?

    init {
        name = GsonUtil.keyAsStr(obj, "label")
        desc = GsonUtil.keyAsStr(obj, "desc")
        info = if (desc != null) processFlags(desc) else null
        fill = GsonUtil.keyAsStr(obj, "fillcolor")
        outline = GsonUtil.keyAsStr(obj, "color")
        x = GsonUtil.arrToIntArr(GsonUtil.keyAsArr(obj, "x"))
        z = GsonUtil.arrToIntArr(GsonUtil.keyAsArr(obj, "z"))
        area = Funcs.calcArea(x, z)
    }

    companion object {
        val whitelist = Safelist().addAttributes("a", "href")
        fun processFlags(str: String): List<String> {
            return GsonUtil.strArrAsStream(str.split("<br />".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
                .map { e: String? -> Jsoup.clean(e, whitelist) }
                .collect(Collectors.toList())
        }
    }
}