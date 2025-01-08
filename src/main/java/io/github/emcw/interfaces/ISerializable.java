package io.github.emcw.interfaces;

import static io.github.emcw.utils.GsonUtil.serialize;

@SuppressWarnings("unused")
public interface ISerializable {
    default String asString() {
        return serialize(this);
    }
}