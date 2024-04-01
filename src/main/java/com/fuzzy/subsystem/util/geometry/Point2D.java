package com.fuzzy.subsystem.util.geometry;

public class Point2D implements Cloneable {
    public static final Point2D[] EMPTY_ARRAY = new Point2D[0];
    public int x;
    public int y;

    public Point2D() {
    }

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point2D clone() {
        return new Point2D(x, y);
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != super.getClass())
            return false;
        return equals((Point2D) o);
    }

    public boolean equals(Point2D p) {
        return equals(p.x, p.y);
    }

    public boolean equals(int x, int y) {
        return (this.x == x) && (this.y == y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        return "[x: " + x + " y: " + y + "]";
    }
}