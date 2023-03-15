package io.github.emcw.interfaces;

import static io.github.emcw.utils.GsonUtil.serialize;

public interface ISerializable {
    default String asString() {
        return serialize(this);
    }
}
