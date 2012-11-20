package com.gravity.map.tiles;

import com.gravity.geom.Rect;
import com.gravity.map.StaticCollidable;

public class BouncyTile extends StaticCollidable {

    public BouncyTile(Rect shape) {
        super(shape);
    }

    @Override
    public String toString() {
        return "BouncyTile [" + super.toString() + "]";
    }
}
