package com.gravity.physics;

import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.geom.Rect;

public class VelocityCollidable implements Collidable {

    private Rect position;
    private float velX;
    private float velY;

    public VelocityCollidable(Rect pos, float velX, float velY) {
        this.position = pos;
        this.velX = velX;
        this.velY = velY;
    }

    @Override
    public Vector2f getPosition(float millis) {
        return position.translate(velX * millis, velY * millis).getPosition();
    }

    @Override
    public Rect getRect(float millis) {
        return position.translate(velX * millis, velY * millis);
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
