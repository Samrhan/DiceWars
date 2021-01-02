package com.dicewars.Models;

public class Coordonnee implements Cloneable {
    private final int x;
    private final int y;
    private TerritoireModel parent = null;
    private boolean free = true;

    public Coordonnee(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TerritoireModel getParent() {
        return this.parent;
    }

    public void setParent(TerritoireModel parent) {
        this.parent = parent;
        this.free = false;
    }

    public boolean isFree() {
        return free;
    }

    public void occupy() {
        free = false;
    }

    public Coordonnee clone() {
        try {
            return (Coordonnee) super.clone();
        } catch (CloneNotSupportedException ignored) {
        }
        return null;
    }
}
