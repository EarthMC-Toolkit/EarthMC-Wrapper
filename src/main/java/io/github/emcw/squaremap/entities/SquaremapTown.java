package io.github.emcw.squaremap.entities;

import io.github.emcw.interfaces.IGsonSerializable;
import static io.github.emcw.utils.GsonUtil.*;

import com.google.gson.JsonObject;

import java.awt.*;
import java.util.Set;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "LombokGetterMayBeUsed"})
public class SquaremapTown implements IGsonSerializable {
    @Getter String name, nation, mayor, wiki;
    @Getter Integer area;
    @Getter SquaremapLocation location;
    @Getter Set<SquaremapResident> residents;
    @Getter Flags flags;
    @Getter Color fill, outline;

    /**
     * Takes a basic parsed marker and parses it further to create a full object representative
     * of a Town on a Squaremap map, with some helper methods attached.
     */
    public SquaremapTown(SquaremapMarker marker) {
        this.name = marker.townName;
        this.nation = marker.nationName;
        this.mayor = marker.mayor;
        this.wiki = marker.townWiki;

        this.location = marker.location;
        this.area = marker.area;

        this.flags = new Flags(marker.PUBLIC, marker.PVP);

        this.fill = getColour(marker.fillColor);
        this.outline = getColour(marker.color);
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
}