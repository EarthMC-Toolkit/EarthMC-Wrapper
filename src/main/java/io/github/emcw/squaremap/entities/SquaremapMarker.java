package io.github.emcw.squaremap.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.emcw.utils.Funcs;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static io.github.emcw.utils.Funcs.roundToNearest16;
import static io.github.emcw.utils.GsonUtil.*;

/**
 * Represents a map marker from the <a href="https://map.earthmc.net/tiles/minecraft_overworld/markers.json">Squaremap</a> API.
 * When constructed, its values are set by parsing info from its respective object (the passed argument) in the response data.
 * <br><br>
 * It is responsible for extracting relevent info from the {@code tooltip} and {@code popup} HTML strings
 * and then storing the semi-raw data which should be fully parsed later.
 * <br><br>
 * You could look at this class as being somewhere between a raw JsonObject marker and a fully fledged
 * {@link SquaremapTown} or {@link SquaremapNation}.
 * @see JsonObject
 */
@SuppressWarnings("unused")
public class SquaremapMarker {
    public final String townName, nationName;

    public final String color, fillColor;
    public final String residents, councillors;
    public final String townWiki, nationWiki;

    public final String mayor;
    public final String founded;
    public final String board;

    public final boolean isCapital;
    public final boolean PVP, PUBLIC;

    public final JsonArray points;
    public final Pair<int[], int[]> bounds;

    public final SquaremapLocation location;
    public final int area;

    public SquaremapMarker(JsonObject rawMarkerObj) {
        // Parse methods extract only the data as seen on the map, nothing extra.
        // All of their properties are primitives. Any additional parsing should be done later.
        JsonObject popup = parsePopup(keyAsStr(rawMarkerObj, "popup"));
        JsonObject tooltip = parseTooltip(keyAsStr(rawMarkerObj, "tooltip"));

        townName = keyAsStr(tooltip, "townName");
        nationName = keyAsStr(tooltip, "nationName");
        board = keyAsStr(tooltip, "board");

        points = keyAsArr(rawMarkerObj, "points");

        // Not sure how exactly points work yet and if we should even flatten them, but oh well.
        bounds = getBounds(flattenJsonArr(points, 2));

        int[] xBounds = bounds.getFirst();
        int[] zBounds = bounds.getSecond();

        location = SquaremapLocation.of(xBounds, zBounds);
        area = Funcs.calcArea(xBounds, zBounds);

        color = keyAsStr(rawMarkerObj, "color");
        fillColor = keyAsStr(rawMarkerObj, "fillColor");

        mayor = keyAsStr(popup, "mayor");

        residents = keyAsStr(popup, "residents");
        councillors = keyAsStr(popup, "councillors");
        founded = keyAsStr(popup, "founded");
        townWiki = keyAsStr(popup, "townWiki");
        nationWiki = keyAsStr(popup, "nationWiki");

        isCapital = Boolean.TRUE.equals(keyAsBool(tooltip, "capital"));

        // Flags
        PVP = Boolean.TRUE.equals(keyAsBool(popup, "pvp"));
        PUBLIC = Boolean.TRUE.equals(keyAsBool(popup, "public"));
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

    @Nullable
    public static Set<String> getNamesFromString(String names) {
        if (names == null || names.isEmpty()) return null;
        return Set.of(names.split(", "));
    }

    @Nullable
    public Set<String> getResidentNames() {
        return getNamesFromString(this.residents);
    }

    @Nullable
    public Set<String> getCouncillorNames() {
        return getNamesFromString(this.councillors);
    }

    // Extracts town name, nation name and board.
    // We can also tell if it's a capital via the brackets content unlike the popup.
    public static JsonObject parseTooltip(String tooltipStr) {
        Pattern pattern = Pattern.compile("<b>(.*?)</b>");
        Matcher matcher = pattern.matcher(tooltipStr);

        // Town name is between the first set of <b></b>
        // We don't use JSoup here because stupid names like "<eculon>" are stripped since they get recognized as tags.
        String townName = matcher.find() ? matcher.group(1) : null;

        Document doc = Jsoup.parse(tooltipStr);

        // Nation name is in the brackets, after the member/capital prefix.
        String divContent = doc.select("div").text();

        boolean isCapital = divContent.contains("(Capital of");
        boolean isMember = divContent.contains("(Member of");

        String nationName = null;
        if (isCapital || isMember) {
            try {
                nationName = divContent.substring(isCapital ?
                    divContent.indexOf("(Capital of ") + 12 : divContent.indexOf("(Member of ") + 11,
                    divContent.lastIndexOf(")")
                );
            } catch (Exception e) {
                new Throwable(
                    "\n  Nation name not set. Index of div content out of bounds.\n  " +
                    "Content: " + divContent + "\n"
                ).printStackTrace();
            }
        }

        // Extract the board (text inside the <i> tag).
        String board = doc.select("i").text();

        JsonObject parsed = new JsonObject();
        parsed.addProperty("townName", townName);
        parsed.addProperty("nationName", nationName);
        parsed.addProperty("board", board);
        parsed.addProperty("capital", isCapital);

        return parsed;
    }

    // Extracts mayor, residents, councillors, founded date, flags (pvp, public) and wiki links.
    // Can also extract what the tooltip can, but likely with more hassle.
    public static JsonObject parsePopup(String popupStr) {
        Document doc = Jsoup.parse(popupStr);

        Element div = tryGetDiv(doc);
        if (div == null) return null; // Something went wrong.

        //#region Extract info from town/nation elements
        Element span = div.selectFirst("span");

        // As long as we have a <span>, get it's first and second <a> tags.
        Element townEl = span == null ? null : span.selectFirst("a");
        Element nationEl = span == null ? null : span.selectFirst("a:nth-of-type(2)");

        String townHref = townEl != null ? townEl.attr("href") : null;
        String nationHref = nationEl != null ? nationEl.attr("href") : null;

        // These work, but not needed because tooltip handles it.
        // String townText = townEl != null ? townEl.text() : null;
        // String nationText = nationEl != null ? nationEl.text() : null;
        //#endregion

        //#region Extract fields ("Key: value") from the div
        String divContent = div.wholeText();
        Map<String, String> fields = extractFields(divContent);
        //#endregion

        //#region Build parsed object
        JsonObject parsed = new JsonObject();
        parsed.addProperty("townWiki", townHref);
        parsed.addProperty("nationWiki", nationHref);
        parsed.addProperty("mayor", fields.get("Mayor"));
        parsed.addProperty("founded", fields.get("Founded"));
        parsed.addProperty("residents", fields.get("Residents"));
        parsed.addProperty("councillors", fields.get("Councillors"));
        parsed.addProperty("pvp", fields.get("PVP"));
        parsed.addProperty("public", fields.get("Public"));
        //#endregion

        return parsed;
    }

    @Nullable
    static Element tryGetDiv(Document doc) {
        Element div = doc.selectFirst("div.infowindow");
        if (div != null) return div;

        // Fallback to regular div since infowindow class could be removed at any point.
        return doc.selectFirst("div");
    }

    @NotNull
    private static Map<String, String> extractFields(String text) {
        Map<String, String> details = new HashMap<>();

        // Define regex pattern to match the "label: value" format
        Pattern pattern = Pattern.compile("(Mayor|Councillors|Founded|PVP|Public):\\s*(.*)");
        Matcher matcher = pattern.matcher(text);

        // Extract matches and put them in the map
        while (matcher.find()) {
            String label = matcher.group(1);
            String value = matcher.group(2).trim();

            details.put(label, value);
        }

        // Handle the Residents field separately as it contains the count
        // as the first element, which we dont want - only the names.
        pattern = Pattern.compile("Residents:\\s*(\\d+)\\s*(.*)");
        matcher = pattern.matcher(text);

        if (matcher.find()) {
            String residents = matcher.group(2).trim();
            details.put("Residents", residents);
        }

        return details;
    }
}