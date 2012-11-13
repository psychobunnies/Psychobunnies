package com.gravity.map;

import com.gravity.geom.Rect;

public class BouncyTile extends StaticCollidable {

    public BouncyTile(Rect shape) {
        super(shape);
    }

    @Override
    public String toString() {
        return "BouncyTile [" + super.toString() + "]";
    }
}
