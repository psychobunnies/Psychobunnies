package com.gravity.entity;

import java.util.Collection;

import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.map.StaticCollidable;
import com.gravity.physics.Collidable;
import com.gravity.physics.RectCollision;

/**
 * Represents an entity which triggers text displaying on the screen.
 * 
 * @author phulin
 */
public class TriggeredTextCollidable extends StaticCollidable {
    private TriggeredText triggeredText;

    public TriggeredTextCollidable(Rect shape, TriggeredText triggeredText) {
        super(shape);
        this.triggeredText = triggeredText;
    }

    @Override
    public void handleCollisions(float ticks, Collection<RectCollision> collisions) {
        for (RectCollision c : collisions) {
            if (c.getOtherEntity(this) instanceof Player) {
                triggeredText.trigger();
            }
        }
    }

    @Override
    public void rehandleCollisions(float ticks, Collection<RectCollision> collisions) {
        // No-op
    }

    @Override
    public String toString() {
        return "TriggeredTextCollidable [shape=" + shape + "]";
    }

    @Override
    public boolean causesCollisionsWith(Collidable other) {
        return false;
    }

}
