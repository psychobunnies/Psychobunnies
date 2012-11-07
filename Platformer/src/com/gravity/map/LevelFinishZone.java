package com.gravity.map;

import java.util.Collection;

import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.physics.RectCollision;
import com.gravity.root.GameplayControl;

public class LevelFinishZone extends TileWorldCollidable {
    private final GameplayControl control;

    public LevelFinishZone(Rect shape, GameplayControl control) {
        super(shape);
        this.control = control;
    }

    @Override
    public void handleCollisions(float ticks, Collection<RectCollision> collisions) {
        for (RectCollision coll : collisions) {
            if (coll.getOtherEntity(this) instanceof Player) {
                control.playerFinishes((Player) coll.getOtherEntity(this));
            }
        }
    }

    @Override
    public String toString() {
        return "LevelFinishZone [" + super.toString() + "]";
    }
}
