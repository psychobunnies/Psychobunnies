package com.gravity.physics;

import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.geom.Rect;

public class VelocityCollidable extends AbstractCollidable {

    public VelocityCollidable(Rect pos, float velX, float velY) {
        super(new PhysicalState(pos, new Vector2f(velX, velY)));
    }

    @Override
    public void handleCollisions(float millis, Collection<RectCollision> collection) {
        // No-op
    }

    @Override
    public void rehandleCollisions(float millis, Collection<RectCollision> collisions) {
        // No-op
    }

    @Override
    public boolean causesCollisionsWith(Collidable other) {
        return true;
    }

}
