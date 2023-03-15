package io.github.emcw.objects;

import com.google.gson.JsonObject;
import io.github.emcw.interfaces.IPlayerCollective;
import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.github.emcw.utils.GsonUtil.*;

public class Town extends Base<Town> implements IPlayerCollective, ISerializable {
    @Getter String mayor, nation;
    @Getter Integer area;
    @Getter Location location;
    @Getter List<Resident> residents;
    @Getter Flags flags;
    @Getter Color fill, outline;

    public Town(JsonObject obj) {
        super();
        setInfo(this, keyAsStr(obj, "name"));

        this.nation = keyAsStr(obj, "nation");
        this.mayor = keyAsStr(obj, "mayor");
        this.residents = Resident.fromArr(obj.getAsJsonArray("residents"));

        this.location = Location.of(obj);
        this.area = keyAsInt(obj, "area");
        this.flags = new Flags(obj);

        this.fill = getColour(keyAsStr(obj, "fill"));
        this.outline = getColour(keyAsStr(obj, "outline"));
    }

    static class Flags {
        Boolean PVP, EXPLOSIONS, FIRE, CAPITAL, MOBS, PUBLIC;

        Flags(JsonObject obj) {
            this.PVP = keyAsBool(obj, "pvp");
            this.EXPLOSIONS = keyAsBool(obj, "explosions");
            this.FIRE = keyAsBool(obj, "fire");
            this.CAPITAL = keyAsBool(obj, "capital");
            this.MOBS = keyAsBool(obj, "mobs");
            this.PUBLIC = keyAsBool(obj, "public");
        }
    }

    public Map<String, Resident> onlineResidents() {
        return (Map<String, Resident>) onlineResidents(residents, parent);
    }

    public boolean nationless() {
        return this.nation == null;
    }

    Color getColour(String hex) {
        return Color.decode(hex == null ? defaultColour() : hex);
    }

    String defaultColour() {
        return defaultColour(this.nation);
    }

    private static String defaultColour(String nationName) {
        return Objects.equals(nationName, "No Nation") ? "#89C500" : "3FB4FF";
    }
}