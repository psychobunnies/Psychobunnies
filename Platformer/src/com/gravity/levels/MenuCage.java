package com.gravity.levels;

import com.gravity.geom.Rect;

public class MenuCage {
    private Rect rect;
    private int state;

    public MenuCage(Rect rect, int state) {
        this.rect = rect;
        this.state = state;
    }

    public Rect getRect() {
        return rect;
    }

    public int getToState() {
        return state;
    }

    public boolean intersects(Rect... rects) {
        for (Rect rect : rects) {
            if (!rect.intersects(this.rect)) {
                return false;
            }
        }
        return true;
    }
}
