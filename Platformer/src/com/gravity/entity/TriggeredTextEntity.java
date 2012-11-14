package com.gravity.entity;

import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.physics.Collidable;
import com.gravity.physics.RectCollision;

/**
 * Represents an entity which triggers text displaying on the screen.
 * 
 * @author phulin
 */
public class TriggeredTextEntity implements Collidable {
    private Rect shape;
    private TriggeredText triggeredText;
    
    public TriggeredTextEntity(Rect shape, TriggeredText triggeredText) {
        this.shape = shape;
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
    public Vector2f getPosition(float millis) {
        return shape.getPosition();
    }
    
    @Override
    public Rect getRect(float millis) {
        return shape;
    }
    
    @Override
    public String toString() {
        return "TriggeredTextEntity [shape=" + shape + "]";
    }

    @Override
    public boolean causesCollisionsWith(Collidable other) {
        return false;
    }
}
