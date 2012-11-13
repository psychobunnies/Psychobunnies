package com.gravity.map;

import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.geom.Rect;
import com.gravity.physics.Collidable;
import com.gravity.physics.RectCollision;
import com.gravity.root.UpdateCycling;

/**
 * Represents a moving platform, e.g.
 *
 * @author phulin
 */

public class MovingCollidable implements Collidable, UpdateCycling {

    private Rect origShape;
    private float velX, velY;
    private float timeSinceStart;
    private int tileWidth, tileHeight;
    private int transX, transY;

    public MovingCollidable(int tileWidth, int tileHeight, Rect shape,
            int transX, int transY, float speed) {
        //System.out.println("making MC");
        this.origShape = shape;
        this.transX = transX;
        this.transY = transY;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        float normalizer = (float) (1 / Math.sqrt(transX * transX + transY * transY) / 1000.0);
        this.velX = transX * tileWidth * normalizer * speed;
        this.velY = transY * tileHeight * normalizer * speed;

        this.timeSinceStart = 0;
    }

    public Vector2f getOrigPosition() {
        return origShape.getPoint(Rect.Corner.BOTLEFT);
    }

    @Override
    public Vector2f getPosition(float millis) {
        return getRect(millis).getPoint(Rect.Corner.BOTLEFT);
    }

    @Override
    public Rect getRect(float millis) {
        float t = timeSinceStart + millis;
        return origShape.translate(velX * t, velY * t);
    }

    @Override
    public void handleCollisions(float millis, Collection<RectCollision> collection) {
        // no-op
    }

    @Override
    public void rehandleCollisions(float millis, Collection<RectCollision> collisions) {
        // no-op
    }

    @Override
    public boolean causesCollisionsWith(Collidable other) {
        return true;
    }

    @Override
    public void finishUpdate(float millis) {
        timeSinceStart += millis;
    }

    @Override
    public void startUpdate(float millis) {
        // no-op
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }
}
