package io.github.emcw.squaremap.entities;

import com.google.gson.JsonObject;

import io.github.emcw.common.Direction;
import io.github.emcw.common.Entity;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.interfaces.IGsonSerializable;
import lombok.Getter;

import static io.github.emcw.utils.GsonUtil.*;

@SuppressWarnings({"unused", "LombokGetterMayBeUsed"})
public class SquaremapOnlinePlayer extends Entity implements ILocatable<SquaremapOnlinePlayer>, IGsonSerializable {
    @Getter private final String displayName;
    @Getter private final String world;
    @Getter private final Integer yaw;
    @Getter private final SquaremapLocation location;

    /**
     * Use when a class extends this class and other data needs to be merged.
     * For example, we call this in {@link SquaremapOnlineResident} since it extends us.
     * @param op The existing online player.
     */
    public SquaremapOnlinePlayer(SquaremapOnlinePlayer op) {
        super(op.getUuid(), op.getName());

        this.displayName = op.getDisplayName();
        this.world = op.getWorld();
        this.yaw = op.getYaw();
        this.location = op.getLocation();
    }

    /**
     * Use when we need to create a new online player from raw data.
     * @param opInfo The JSON data containing info about an online player.
     */
    public SquaremapOnlinePlayer(JsonObject opInfo) {
        super(keyAsStr(opInfo, "uuid"), keyAsStr(opInfo, "name"));

        this.displayName = keyAsStr(opInfo, "displayName");
        this.world = keyAsStr(opInfo, "world");
        this.yaw = keyAsInt(opInfo, "yaw");
        this.location = new SquaremapLocation(keyAsInt(opInfo, "x"), keyAsInt(opInfo, "z"));
    }

    /**
     * If this player has set a nickname.
     * @return true/false if {@link #displayName} is same as their account {@link #name}.
     */
    public boolean hasCustomName() {
        String realName = this.name == null ? "" : this.name;
        return displayName != null && !displayName.equals(realName);
    }

    /**
     * If this player is visible on the map.
     * @return true/false if {@link #world} is "earth" and player is not under a block.
     */
    public boolean isInOverworld() {
        return this.world.equals("minecraft_overworld");
    }

//    /**
//     * Essentially the opposite of {@link #visible}.
//     * <p><b>NOTE:</b>
//     * This returns true for players under a tree, in the nether etc.
//     * @return true/false if {@link #world} is NOT "earth" and {@link #location} is 0, 64, 0.
//     */
//    public boolean hidden() {
//        return locationIsDefault() && !visible();
//    }
//
//    /**
//     * Whether this player is located at the default map location.
//     * @return true/false if {@link #location} is 0, 0
//     */
//    public boolean locationIsDefault() {
//        return this.location.x == 0 && this.location.z == 0;
//    }

    public Direction facingDirection() throws IllegalArgumentException {
        // Normalize the yaw to a value between 0 and 360 degrees
        float normalized = (this.yaw % 360 + 360) % 360;

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
        throw new IllegalArgumentException("Invalid yaw value: " + this.yaw);
    }
}