package io.github.emcw.objects;

import com.google.gson.JsonObject;
import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.emcw.core.EMCWrapper.instance;
import static io.github.emcw.utils.GsonUtil.*;

public class Player extends Base<Player> implements ISerializable {
    @Getter String nickname;
    @Getter Location location = null;

    private transient String world = null;
    private transient Boolean isResident = false;

    public Player(JsonObject obj) {
        super();
        init(obj, false);
        setLocation(obj, false);
    }

    public Player(JsonObject obj, Boolean resident) {
        super();
        init(obj, resident);
        setLocation(obj, false);
    }

    public Player(JsonObject obj, Boolean resident, Boolean parsed) {
        super();
        init(obj, resident);
        setLocation(obj, parsed);
    }

    public void init(JsonObject obj, @NotNull Boolean resident) {
        setInfo(this, keyAsStr(obj, "name"));
        nickname = keyAsStr(obj, "nickname");
        world = keyAsStr(obj, "world");
        isResident = resident;
    }

    public void setLocation(JsonObject obj, Boolean parsed) {
        Location loc;

        if (parsed) loc = Location.fromObj(obj.getAsJsonObject("location"));
        else loc = Location.fromObj(obj);

        if (loc.valid()) location = loc;
    }

    public boolean hasCustomNickname() {
        return nickname != null && !Objects.equals(nickname, name);
    }

    public boolean underground() {
        return hidden() && !Objects.equals(world, "earth");
    }

    public boolean hidden() {
        return location.y == 64 && location.x == 0 && location.z == 0;
    }

    public boolean isResident() {
        return isResident;
    }

    public boolean online(String map) {
        return online(map, name);
    }

    public static boolean online(String mapName, String playerName) {
        EMCMap map = Objects.equals(mapName, "nova") ? instance().getNova() : instance().getAurora();
        return map.Players.getOnline(playerName) != null;
    }
}