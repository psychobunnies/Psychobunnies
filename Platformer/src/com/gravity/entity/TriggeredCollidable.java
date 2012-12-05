package com.gravity.entity;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
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
public class TriggeredCollidable extends StaticCollidable {
    private List<TriggeredBase> triggeredBases;

    public TriggeredCollidable(Rect shape) {
        super(shape);
        this.triggeredBases = Lists.newLinkedList();
    }

    public void addBase(TriggeredBase base) {
        triggeredBases.add(base);
    }

    @Override
    public void handleCollisions(float ticks, Collection<RectCollision> collisions) {
        for (RectCollision c : collisions) {
            if (c.getOtherEntity(this) instanceof Player) {
                for (TriggeredBase base : triggeredBases) {
                    base.trigger();
                }
            }
        }
    }

    @Override
    public void rehandleCollisions(float ticks, Collection<RectCollision> collisions) {
        // No-op
    }

    @Override
    public String toString() {
        return "TriggeredCollidable [triggeredBases=" + triggeredBases + ", shape=" + shape + "]";
    }

    @Override
    public boolean causesCollisionsWith(Collidable other) {
        return false;
    }

}
