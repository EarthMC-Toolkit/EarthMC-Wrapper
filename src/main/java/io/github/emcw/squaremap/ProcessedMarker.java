package io.github.emcw.squaremap;

import com.google.gson.JsonObject;
import io.github.emcw.utils.parsers.BaseParser;

import java.util.List;

import static io.github.emcw.utils.Funcs.calcArea;
import static io.github.emcw.utils.GsonUtil.*;

/**
 * Represents a map marker that gets "processed" when constructed, i.e. has its values set by
 * parsing info from its respective object (the passed argument) in the raw map response.
 * <br><br>
 * You could look at this class as being somewhere between a raw JsonObject marker and a fully fledged
 * {@link io.github.emcw.map.entities.Town} or {@link io.github.emcw.map.entities.Nation}.
 * @see JsonObject
 */
@SuppressWarnings("unused")
public class ProcessedMarker {
//    public final String townName;
//    public final String nationName;
//    public final String board;

//    public final String fill, outline;
//
//    public final int[] x, z;
//    public final int area;
//
//    public final List<String> info;

    public ProcessedMarker(JsonObject rawMarkerObj) {
        // Parse popup
//        var popupInfo = parsePopup(rawMarkerObj.get("popup").getAsString());
//        var popupInfo = parseTooltip(rawMarkerObj.get("tooltip").getAsString());

//        this.townName = keyAsStr(popupInfo, "townName");
//        this.nationName = keyAsStr(popupInfo, "nationName");
//        this.board = keyAsStr(popupInfo, "board");
//
//        this.fill = keyAsStr(obj, "fillcolor");
//        this.outline = keyAsStr(obj, "color");
//
//        this.x = arrToIntArr(keyAsArr(obj, "x"));
//        this.z = arrToIntArr(keyAsArr(obj, "z"));
//        this.area = calcArea(this.x, this.z);
    }

    // Extracts mayor, residents, councillors, founded date, flags (pvp, public) and wiki links.
    // Can also extract what the tooltip can, but likely with more hassle.
    public JsonObject parsePopup(String popupStr) {
        JsonObject parsed = new JsonObject();

        return parsed;
    }

    // Extracts town name, nation name and board.
    // We can also tell if it's a capital via the brackets content unlike the popup.
    public JsonObject parseTooltip(String tooltipStr) {
        JsonObject parsed = new JsonObject();

        return parsed;
    }
}