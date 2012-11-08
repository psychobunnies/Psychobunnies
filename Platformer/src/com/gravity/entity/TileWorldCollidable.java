package com.gravity.entity;

import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.geom.Rect;
import com.gravity.physics.Collidable;
import com.gravity.physics.RectCollision;

/**
 * Represents a convex shape in map terrain for collision detection.
 * 
 * @author xiao
 */
public class TileWorldCollidable implements Collidable {
    private Rect shape;

    public TileWorldCollidable(Rect shape) {
        this.shape = shape;
    }

    @Override
    public void handleCollisions(float ticks, Collection<RectCollision> collisions) {
        // No-op
    }

    @Override
    public void rehandleCollisions(float ticks, Collection<RectCollision> collisions) {
        // No-op
    }

    @Override
    public Vector2f getPosition(float millis) {
        return shape.getPosition();
    }

    @Override
    public Rect getRect(float millis) {
        return shape;
    }

    @Override
    public String toString() {
        return "TileWorldCollidable [shape=" + shape + "]";
    }

    @Override
    public boolean isPassThrough() {
        return false;
    }

}
