package io.github.emcw.common;

import io.github.emcw.interfaces.IGsonSerializable;
import lombok.Getter;

@Getter
public class Point2D implements IGsonSerializable {
    private final int X;
    private final int Z;

    public Point2D(int x, int z) {
        this.X = x;
        this.Z = z;
    }

    @Override
    public String toString() {
        return String.format("[X: %d, Z: %d]", X, Z);
    }
}