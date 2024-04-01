package com.fuzzy.subsystem.util.geometry;

public abstract interface Shape {
    boolean isInside(int paramInt1, int paramInt2);

    boolean isInside(int paramInt1, int paramInt2, int paramInt3);

    int getXmax();

    int getXmin();

    int getYmax();

    int getYmin();

    int getZmax();

    int getZmin();
}