package io.github.emcw.interfaces;

import io.github.emcw.utils.GsonUtil;

@SuppressWarnings("unused")
public interface IGsonSerializable {
    /**
     * Serializes this object into its equivalent JSON representation.
     * Should only used as an alternative to {@link Object#toString toString} when appropriate.
     * @return A new JSON formatted string.
     */
    default String asString() {
        return GsonUtil.serialize(this);
    }
}