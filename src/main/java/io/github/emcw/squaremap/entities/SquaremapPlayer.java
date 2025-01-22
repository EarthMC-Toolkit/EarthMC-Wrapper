package io.github.emcw.squaremap.entities;

import com.google.gson.JsonObject;

import io.github.emcw.Direction;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.emcw.utils.GsonUtil.*;

@SuppressWarnings("unused")
public class SquaremapPlayer implements ISerializable, ILocatable<SquaremapPlayer> {
    @Getter private String name, nickname;
    @Getter private SquaremapLocation location = null;
    @Getter private Integer yaw;

    private transient String world = null;
    @Setter transient boolean isResident = false;

    public SquaremapPlayer(JsonObject obj, Boolean resident) {
        init(obj, resident);
        setLocation(obj, false);
    }

    SquaremapPlayer(JsonObject obj, Boolean resident, boolean parsed) {
        init(obj, resident);
        setLocation(obj, parsed);
    }

    public SquaremapPlayer(@NotNull SquaremapPlayer player) {
        name = player.getName();
        nickname = player.getNickname();
        location = player.getLocation();
        yaw = player.getYaw();
    }

    private void init(JsonObject obj, boolean resident) {
        name = keyAsStr(obj, "name");
        nickname = keyAsStr(obj, "nickname");
        world = keyAsStr(obj, "world");
        isResident = resident;
    }

    void setLocation(JsonObject obj, boolean parsed) {
        SquaremapLocation loc = SquaremapLocation.fromObj(parsed ? obj.getAsJsonObject("location") : obj);
        if (loc.valid()) {
            location = loc;
        }
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
        return isResident;
    }

    public Direction facingDirection() throws IllegalArgumentException {
        // Normalize the yaw to a value between 0 and 360 degrees
        float normalized = (yaw % 360 + 360) % 360;

        // Determine direction based on normalized value
        if (337.5 <= normalized || 0 <= normalized && normalized < 22.5) {
            return Direction.SOUTH;
        } else if (22.5 <= normalized && normalized < 67.5) {
            return Direction.SOUTHWEST;
        } else if (67.5 <= normalized && normalized < 112.5) {
            return Direction.WEST;
        } else if (112.5 <= normalized && normalized < 157.5) {
            return Direction.NORTHWEST;
        } else if (157.5 <= normalized && normalized < 202.5) {
            return Direction.NORTH;
        } else if (202.5 <= normalized && normalized < 247.5) {
            return Direction.NORTHEAST;
        } else if (247.5 <= normalized && normalized < 292.5) {
            return Direction.EAST;
        } else if (292.5 <= normalized && normalized < 337.5) {
            return Direction.SOUTHEAST;
        }

        // This should never occur since yaw is an integer and will always be
        throw new IllegalArgumentException("Invalid yaw value: " + yaw);
    }
}