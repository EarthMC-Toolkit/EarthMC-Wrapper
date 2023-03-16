package io.github.emcw.objects;

import com.google.gson.JsonObject;
import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;

import java.util.Objects;

import static io.github.emcw.core.EMCWrapper.instance;
import static io.github.emcw.utils.GsonUtil.*;

public class Player extends Base<Player> implements ISerializable {
    @Getter String nickname;
    @Getter Location location = null;

    String world = null;
    private transient Boolean isResident = false;

    public Player(JsonObject obj) {
        super();
        init(obj, false);
    }

    public Player(JsonObject obj, Boolean resident) {
        super();
        init(obj, resident);
    }

    public void init(JsonObject obj, Boolean resident) {
        setInfo(this, keyAsStr(obj, "name"));
        nickname = keyAsStr(obj, "nickname");

        if (resident) isResident = true;
        else {
            world = keyAsStr(obj, "world");
            location = Location.fromObj(obj);
        }
    }

    boolean hidden() {
        return location.y == 64 &&
               location.x == 0 &&
               location.z == 0;
    }

    boolean underground() {
        return !Objects.equals(world, "earth");
    }

    public boolean online(String map) {
        return online(map, name);
    }

    private boolean isResident() {
        return isResident;
    }

    public static boolean online(String mapName, String playerName) {
        EMCMap map = Objects.equals(mapName, "nova") ? instance().getNova() : instance().getAurora();
        return map.Players.getOnline(playerName) != null;
    }
}