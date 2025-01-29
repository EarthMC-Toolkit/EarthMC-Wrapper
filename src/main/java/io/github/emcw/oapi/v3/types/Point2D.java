package io.github.emcw.oapi.v3.types;

import io.github.emcw.interfaces.IGsonSerializable;

public class Point2D implements IGsonSerializable {
    public final int X;
    public final int Z;

    public Point2D(int x, int z) {
        this.X = x;
        this.Z = z;
    }
}