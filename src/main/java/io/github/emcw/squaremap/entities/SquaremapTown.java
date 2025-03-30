package io.github.emcw.squaremap.entities;

import io.github.emcw.common.Point2D;
import io.github.emcw.interfaces.IGsonSerializable;
import static io.github.emcw.utils.GsonUtil.*;

import com.google.gson.JsonObject;

import kotlin.Pair;
import lombok.Getter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@SuppressWarnings({"unused"})
public class SquaremapTown implements IGsonSerializable {
    String name, nation, mayor, wiki, board;
    String foundedDate;

    Integer area;
    SquaremapLocation location;
    List<Point2D> points;
    Pair<int[], int[]> bounds;

    List<String> residents;
    List<String> councillors;

    Flags flags;
    Color fill, outline;

    /**
     * Takes a basic parsed marker and parses it further to create a full object representative
     * of a Town on a Squaremap map, with some helper methods attached.
     */
    public SquaremapTown(SquaremapMarker marker, Set<String> residents, Set<String> councillors) {
        this.name = marker.townName;
        this.nation = marker.nationName;
        this.mayor = marker.mayor;
        this.wiki = marker.townWiki;
        this.board = marker.board;

        this.location = marker.location;
        this.area = marker.area;

        this.points = marker.points.asList().stream()
            .map(el -> new Point2D(keyAsInt(el.getAsJsonObject(), "x"), keyAsInt(el.getAsJsonObject(), "x")))
            .toList();

        this.bounds = marker.bounds;

        this.flags = new Flags(marker.PUBLIC, marker.PVP);

        this.fill = getColour(marker.fillColor);
        this.outline = getColour(marker.color);

        this.residents = residents.stream().toList();
        this.councillors = councillors.stream().toList();

        this.foundedDate = marker.founded;
    }

    public static class Flags {
        public final boolean PUBLIC, PVP;
//        public final boolean EXPLOSIONS, FIRE, CAPITAL, MOBS;

        public Flags(JsonObject obj) {
            this.PUBLIC = Boolean.TRUE.equals(keyAsBool(obj, "public"));
            this.PVP = Boolean.TRUE.equals(keyAsBool(obj, "pvp"));
//            EXPLOSIONS = keyAsBool(obj, "explosions");
//            FIRE = keyAsBool(obj, "fire");
//            CAPITAL = keyAsBool(obj, "capital");
//            MOBS = keyAsBool(obj, "mobs");
        }

        public Flags(boolean PUBLIC, boolean PVP) {
            this.PUBLIC = PUBLIC;
            this.PVP = PVP;
        }
    }

    public boolean isNationless() {
        return this.nation == null || this.nation.isEmpty();
    }

    public long getFoundedTimestamp() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
        Date date = sdf.parse(this.foundedDate);

        return date.getTime();
    }

    Color getColour(String hex) {
        return Color.decode(hex == null ? defaultColour() : hex);
    }

    String defaultColour() {
        return defaultColour(this.nation);
    }

    @Contract(pure = true)
    private static @NotNull String defaultColour(String nationName) {
        return nationName.equals("No Nation") ? "#89C500" : "#3FB4FF";
    }

//    public double distanceFrom(Point2D loc) throws NullPointerException {
//        SquaremapLocation townLoc = getLocation();
//        if (!townLoc.isValidPoint()) {
//            throw new NullPointerException(String.format(
//                "Error getting distance from %s. Town location is invalid:\n%s",
//                getName(), townLoc.asString()
//            ));
//        }
//
//        // Safe to create point as we know the town loc is valid.
//        Point2D townPoint = new Point2D(townLoc.getX(), townLoc.getZ());
//        return euclidean(loc, townPoint);
//    }
}