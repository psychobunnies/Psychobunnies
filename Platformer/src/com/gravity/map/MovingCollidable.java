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

    private Rect shape;
    private Vector2f origPosition;
    private int tileWidth, tileHeight;
    private Vector2f vel, finalPosition;
    private boolean reversed;
    
    public MovingCollidable(int tileWidth, int tileHeight, Rect shape,
            int tileTransX, int tileTransY, float speed) {
        //System.out.println("making MC");
        this.shape = shape;
        this.origPosition = shape.getPoint(Rect.Corner.TOPLEFT);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        Vector2f trans = new Vector2f(tileTransX * tileWidth, tileTransY * tileHeight);
        vel = trans.getNormal();
        vel.scale(speed / 1000.0f);
        System.out.println("Vel: " + vel);
        finalPosition = origPosition.copy().add(trans);
        
        reversed = false;
    }

    public Vector2f getOrigPosition() {
        return origPosition;
    }

    @Override
    public Vector2f getPosition(float millis) {
        return getRect(millis).getPoint(Rect.Corner.TOPLEFT);
    }

    @Override
    public Rect getRect(float millis) {
        return getRectWithReversal(millis).rect;
    }
    
    private class RectWithReversal {
        public final boolean reverse;
        public final Rect rect;
        
        public RectWithReversal(boolean reverse, Rect rect) {
            this.reverse = reverse;
            this.rect = rect;
        }
    }
    
    public RectWithReversal getRectWithReversal (float millis) {
        boolean reverse;
        Vector2f position;
        if (reversed) {
            Vector2f potentialResult = shape.translate(vel.copy().scale(-millis)).getPoint(Rect.Corner.TOPLEFT);
            reverse = potentialResult.distance(finalPosition) > origPosition.distance(finalPosition);
            if (reverse) {
                position = origPosition.copy().scale(2.0f).sub(potentialResult);
            } else {
                position = potentialResult;
            }
        } else {
            Vector2f potentialResult = shape.translate(vel.copy().scale(millis)).getPoint(Rect.Corner.TOPLEFT);
            reverse = potentialResult.distance(origPosition) > finalPosition.distance(origPosition);
            if (reverse) {
                position = finalPosition.copy().scale(2.0f).sub(potentialResult);
            } else {
                position = potentialResult;
            }
        }
        return new RectWithReversal(reverse, shape.setPosition(position.x, position.y));
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
        System.out.println("old position: " + shape);
        System.out.println("vel: " + vel + ", millis: " + millis);
        RectWithReversal result = getRectWithReversal(millis);
        reversed = result.reverse;
        shape = result.rect;
        System.out.println("new position: " + shape);
        System.out.println("");
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
