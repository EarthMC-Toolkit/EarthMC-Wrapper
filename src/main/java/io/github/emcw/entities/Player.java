package io.github.emcw.entities;

import com.google.gson.JsonObject;
import io.github.emcw.core.EMCMap;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.interfaces.ISerializable;
import io.github.emcw.utils.GsonUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static io.github.emcw.core.EMCWrapper.instance;
import static io.github.emcw.utils.GsonUtil.*;

public class Player extends BaseEntity<Player> implements ISerializable, ILocatable<Player> {
    @Getter private String nickname;
    @Getter private Location location = null;

    private transient String world = null;
    @Setter transient Boolean isResident = false;

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

    public Player(@NotNull Player player) {
        super();

        setInfo(this, player.getName());
        nickname = player.getNickname();
        location = player.getLocation();
        isResident = player.isResident();
    }

    public void init(JsonObject obj, @NotNull Boolean resident) {
        setInfo(this, keyAsStr(obj, "name"));
        nickname = keyAsStr(obj, "nickname");
        world = keyAsStr(obj, "world");
        isResident = resident;
    }

    public void setLocation(JsonObject obj, @NotNull Boolean parsed) {
        Location loc;

        if (parsed) loc = Location.fromObj(obj.getAsJsonObject("location"));
        else loc = Location.fromObj(obj);

        if (loc.valid()) location = loc;
    }

    public static EMCMap getMap(String name) {
        return Objects.equals(name, "nova") ? instance().getNova() : instance().getAurora();
    }

    public Resident asResident(String mapName) throws MissingEntryException {
        Resident res = getMap(mapName).Residents.single(name);
        return new Resident(GsonUtil.asTree(res), this);
    }

    public boolean hasCustomNickname() {
        return nickname != null && !Objects.equals(nickname, name);
    }

    public boolean aboveGround() {
        return Objects.equals(world, "earth");
    }

    public boolean underground() {
        return locationIsDefault() && !aboveGround();
    }

    public boolean locationIsDefault() {
        return location.y == 64 && location.x == 0 && location.z == 0;
    }

    public boolean isResident() {
        return isResident != null && isResident;
    }

    public boolean online(String map) {
        return getMap(map).Players.online().containsKey(name);
    }

    public static @Nullable Player getOnline(String mapName, String playerName) {
        return getMap(mapName).Players.getOnline(playerName);
    }
}