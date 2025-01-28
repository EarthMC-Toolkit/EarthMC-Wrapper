package io.github.emcw.interfaces;

import io.github.emcw.utils.GsonUtil;

@SuppressWarnings("unused")
public interface IGsonSerializable {
    default String asString() {
        return GsonUtil.serialize(this);
    }
}