package io.github.emcw.squaremap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.emcw.utils.Funcs;
import kotlin.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.github.emcw.map.entities.Location;

import java.util.stream.IntStream;

import static io.github.emcw.utils.Funcs.midrange;
import static io.github.emcw.utils.Funcs.roundToNearest16;
import static io.github.emcw.utils.GsonUtil.*;

/**
 * Represents a map marker from the <a href="https://map.earthmc.net/tiles/minecraft_overworld/markers.json">Squaremap</a> API.
 * When constructed, its values are set by parsing info from its respective object (the passed argument) in the response data.
 * <br><br>
 * You could look at this class as being somewhere between a raw JsonObject marker and a fully fledged
 * {@link io.github.emcw.map.entities.Town} or {@link io.github.emcw.map.entities.Nation}.
 * @see JsonObject
 */
@SuppressWarnings("unused")
public class SquaremapMarker {
    final String townName;
    final String nationName;
    final String board;
    final boolean isCapital;

    final String type;
    final String fill, outline;

    final JsonArray points;

    public SquaremapMarker(JsonObject rawMarkerObj) {
        var popup = parsePopup(keyAsStr(rawMarkerObj, "popup"));
        var tooltip = parseTooltip(keyAsStr(rawMarkerObj, "tooltip"));

        this.townName = keyAsStr(tooltip, "townName");
        this.nationName = keyAsStr(tooltip, "nationName");
        this.board = keyAsStr(tooltip, "board");
        this.isCapital = Boolean.TRUE.equals(keyAsBool(tooltip, "capital"));

        this.fill = keyAsStr(rawMarkerObj, "fillcolor");
        this.outline = keyAsStr(rawMarkerObj, "color");
        this.type = keyAsStr(rawMarkerObj, "type");

        this.points = keyAsArr(rawMarkerObj, "points");
    }

    /**
     * Parses {@link #points} into two seperate arrays, which we call bounds for the sake of simplicity.<br><br>
     * For example:<br>
     * <code>
     *  [{ "x": 500, z": -200 }, { "x": 7000, "z: "80" }]
     * </code>
     * <br><br>
     * Would become:<br>
     * <code>
     *   Pair<[500, 7000], [-200, 80]>
     * </code>
     * @return New pair of int arrays. First = all points on the X axis. Second = all points on the Z axis.
     */
    public static Pair<int[], int[]> getBounds(JsonArray points) {
        int size = points.size();

        int[] xPoints = new int[size];
        int[] zPoints = new int[size];

        IntStream.range(0, size).parallel().forEach(i -> {
            JsonObject point = points.get(i).getAsJsonObject();

            Double x = keyAsDouble(point, "x");
            Double z = keyAsDouble(point, "z");

            if (x != null) xPoints[i] = roundToNearest16(x);
            if (z != null) zPoints[i] = roundToNearest16(z);
        });

        return new Pair<>(xPoints, zPoints);
    }

    public Pair<int[], int[]> getBounds() {
        return getBounds(this.points);
    }

    public Location getLocation() {
        Pair<int[], int[]> bounds = getBounds();

        Integer x = midrange(bounds.getFirst());
        Integer z = midrange(bounds.getSecond());

        return new Location(x, z);
    }

    public int getArea() {
        Pair<int[], int[]> bounds = getBounds();
        return Funcs.calcArea(bounds.getFirst(), bounds.getSecond());
    }

    // Extracts mayor, residents, councillors, founded date, flags (pvp, public) and wiki links.
    // Can also extract what the tooltip can, but likely with more hassle.
    public static JsonObject parsePopup(String popupStr) {
        JsonObject parsed = new JsonObject();

        return parsed;
    }

    // Extracts town name, nation name and board.
    // We can also tell if it's a capital via the brackets content unlike the popup.
    public static JsonObject parseTooltip(String tooltipStr) {
        Document doc = Jsoup.parse(tooltipStr);

        // Town name is between the first set of <b></b>
        String townName = doc.select("b").text();

        // Nation name is in the brackets, after the member/capital prefix.
        String divContent = doc.select("div").text();

        boolean isCapital = divContent.contains("Capital of");
        String nationName = divContent.substring(isCapital ?
            divContent.indexOf("Capital of ") + 11 : divContent.indexOf("Member of ") + 10,
            divContent.lastIndexOf(")")
        );

        // Extract the board (text inside <i>)
        String board = doc.select("i").text();

        JsonObject parsed = new JsonObject();
        parsed.addProperty("townName", townName);
        parsed.addProperty("nationName", nationName);
        parsed.addProperty("board", board);
        parsed.addProperty("capital", isCapital);

        return parsed;
    }
}