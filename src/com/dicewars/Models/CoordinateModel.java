package com.dicewars.Models;

public class CoordinateModel implements Cloneable {
    private final int x;
    private final int y;
    private TerritoryModel parent = null;
    private boolean free = true;

    public CoordinateModel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TerritoryModel getParent() {
        return this.parent;
    }

    public void setParent(TerritoryModel parent) {
        this.parent = parent;
        this.free = false;
    }

    public boolean isFree() {
        return free;
    }

    public void occupy() {
        free = false;
    }

    public CoordinateModel clone() {
        try {
            return (CoordinateModel) super.clone();
        } catch (CloneNotSupportedException ignored) {
        }
        return null;
    }
}
