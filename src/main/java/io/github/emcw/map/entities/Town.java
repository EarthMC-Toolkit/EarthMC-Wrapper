package io.github.emcw.map.entities;

import com.google.gson.JsonObject;

import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static io.github.emcw.utils.GsonUtil.*;

@SuppressWarnings("unused")
public class Town extends BaseEntity<Town> implements ISerializable {
    @Getter String mayor, nation;
    @Getter Integer area;
    @Getter Location location;
    @Getter List<Resident> residents;
    @Getter Flags flags;
    @Getter Color fill, outline;

    public Town(JsonObject obj) {
        super();
        init(obj);
    }

    public Town(@NotNull Capital capital) {
        super();

        setInfo(this, capital.getName());
        location = capital.getLocation();
    }

    void init(JsonObject obj) {
        setInfo(this, keyAsStr(obj, "name"));

        nation = keyAsStr(obj, "nation");
        mayor = keyAsStr(obj, "mayor");
        residents = Resident.fromArr(keyAsArr(obj, "residents"), "name");

        location = Location.of(obj);
        area = keyAsInt(obj, "area");
        flags = new Flags(obj);

        fill = getColour(keyAsStr(obj, "fill"));
        outline = getColour(keyAsStr(obj, "outline"));
    }

    public static class Flags {
        public final Boolean PVP, EXPLOSIONS, FIRE, CAPITAL, MOBS, PUBLIC;

        Flags(JsonObject obj) {
            PVP = keyAsBool(obj, "pvp");
            EXPLOSIONS = keyAsBool(obj, "explosions");
            FIRE = keyAsBool(obj, "fire");
            CAPITAL = keyAsBool(obj, "capital");
            MOBS = keyAsBool(obj, "mobs");
            PUBLIC = keyAsBool(obj, "public");
        }
    }

//    public Map<String, Resident> onlineResidents() {
//
//    }

    public boolean nationless() {
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
        return Objects.equals(nationName, "No Nation") ? "#89C500" : "3FB4FF";
    }
}