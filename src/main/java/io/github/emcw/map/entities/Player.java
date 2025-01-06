package io.github.emcw.map.entities;

import com.google.gson.JsonObject;
import io.github.emcw.EMCMap;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static io.github.emcw.EMCWrapper.instance;
import static io.github.emcw.utils.GsonUtil.*;

@SuppressWarnings("unused")
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

    private void init(JsonObject obj, @NotNull Boolean resident) {
        setInfo(this, keyAsStr(obj, "name"));
        nickname = keyAsStr(obj, "nickname");
        world = keyAsStr(obj, "world");
        isResident = resident;
    }

    public void setLocation(JsonObject obj, @NotNull Boolean parsed) {
        Location loc = parsed ? Location.fromObj(obj.getAsJsonObject("location")) : Location.fromObj(obj);
        if (loc.valid()) location = loc;
    }

    private static EMCMap getMap(String name) {
        return instance().getAurora();
        //return Objects.equals(name, "nova") ? instance().getNova() : instance().getAurora();
    }

    /**
     * <p>Converts this player into a {@link Resident}.<br>
     * Essentially equivalent to a "downcast", adding new fields and methods found in {@link Resident},
     * keeping all existing info the same. </p>
     * @param mapName The map used to retrieve the resident from. If invalid, Aurora will be assumed.
     * @return The {@link Resident} instance if found, otherwise a {@link MissingEntryException}.
     */
    public Resident asResident(String mapName) throws MissingEntryException {
        Resident res = getMap(mapName).Residents.single(name);
        return new Resident(asTree(res), this);
    }

    /**
     * If this player has set a nickname.
     * @return true/false if {@link #nickname} is same as their account {@link #name}.
     */
    public boolean hasCustomNickname() {
        return nickname != null && !Objects.equals(nickname, name);
    }

    /**
     * If this player is visible on the Dynmap.
     * @return true/false if {@link #world} is "earth" and player is not under a block.
     */
    public boolean visible() {
        return Objects.equals(world, "earth");
    }

    /**
     * Essentially the opposite of {@link #visible}.
     * <p><b>NOTE:</b>
     * This returns true for players under a tree, in the nether etc.
     * @return true/false if {@link #world} is NOT "earth" and {@link #location} is 0, 64, 0.
     */
    public boolean hidden() {
        return locationIsDefault() && !visible();
    }

    /**
     * Whether this player is located at the default map location.
     * @return true/false if {@link #location} is 0, 0
     */
    public boolean locationIsDefault() {
        return location.x == 0 && location.z == 0;
    }

    /**
     * Check if this player is also a resident on the map this instance was retrieved from.
     */
    public boolean isResident() {
        return isResident != null && isResident;
    }

    /**
     * <p>Check if this player is online for the given map.</p>
     * @return true/false if the player is online.
     */
    public boolean online(String map) {
        return getMap(map).Players.online().containsKey(name);
    }

    /**
     * <p>Static helper method for retrieving an online {@link Player}.</p>
     * @param mapName The map this player is online in.<br> If invalid map is inputted, Aurora will be assumed.
     * @param playerName The name of the player we want to retrieve.
     * @return A new instance of this class
     */
    public static @Nullable Player getOnline(String mapName, String playerName) {
        return getMap(mapName).Players.getOnline(playerName);
    }
}