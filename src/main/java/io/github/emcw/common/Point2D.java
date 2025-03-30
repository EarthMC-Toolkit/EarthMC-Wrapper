package io.github.emcw.common;

import io.github.emcw.interfaces.IGsonSerializable;
import lombok.Getter;

@Getter
@SuppressWarnings("ClassCanBeRecord")
public class Point2D implements IGsonSerializable {
    private final int x;
    private final int z;

    public Point2D(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("[X: %d, Z: %d]", x, z);
    }
}