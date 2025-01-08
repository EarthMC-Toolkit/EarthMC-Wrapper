package io.github.emcw.oapi.v3.types;

import io.github.emcw.interfaces.ISerializable;

public class Point2D implements ISerializable {
    public final int X;
    public final int Z;

    public Point2D(int x, int z) {
        this.X = x;
        this.Z = z;
    }
}