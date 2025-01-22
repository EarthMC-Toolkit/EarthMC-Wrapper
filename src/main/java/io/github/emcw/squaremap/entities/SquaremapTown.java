package io.github.emcw.squaremap.entities;

import com.google.gson.JsonObject;

import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import java.util.Objects;
import java.util.Set;

import static io.github.emcw.utils.GsonUtil.*;

@SuppressWarnings("unused")
public class SquaremapTown implements ISerializable {
    @Getter String name, nation, mayor;
    @Getter Integer area;
    @Getter SquaremapLocation location;
    @Getter Set<String> residentNames;
    @Getter Flags flags;
    @Getter Color fill, outline;

    public SquaremapTown(SquaremapMarker marker) {
        name = marker.townName;
        nation = marker.nationName;
        mayor = marker.mayor;
        location = marker.location;
        area = marker.area;

        residentNames = Set.of(marker.residents.split(", "));

        flags = new Flags(marker.Public, marker.PVP);

        fill = getColour(marker.fillColor);
        outline = getColour(marker.color);
    }

    public static class Flags {
        public final boolean PUBLIC, PVP;
//        public final boolean EXPLOSIONS, FIRE, CAPITAL, MOBS;

        public Flags(JsonObject obj) {
            PUBLIC = Boolean.TRUE.equals(keyAsBool(obj, "public"));
            PVP = Boolean.TRUE.equals(keyAsBool(obj, "pvp"));
//            EXPLOSIONS = keyAsBool(obj, "explosions");
//            FIRE = keyAsBool(obj, "fire");
//            CAPITAL = keyAsBool(obj, "capital");
//            MOBS = keyAsBool(obj, "mobs");
        }

        public Flags(boolean PUBLIC, boolean PVP) {
            this.PUBLIC = Boolean.TRUE.equals(PUBLIC);
            this.PVP = Boolean.TRUE.equals(PVP);
        }
    }

//    public Map<String, Resident> onlineResidents() {
//
//    }

    public boolean isNationless() {
        return nation == null;
    }

    Color getColour(String hex) {
        return Color.decode(hex == null ? defaultColour() : hex);
    }

    String defaultColour() {
        return defaultColour(nation);
    }

    @Contract(pure = true)
    private static @NotNull String defaultColour(String nationName) {
        return Objects.equals(nationName, "No Nation") ? "#89C500" : "#3FB4FF";
    }
}