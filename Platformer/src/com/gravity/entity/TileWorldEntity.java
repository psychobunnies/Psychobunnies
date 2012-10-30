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
public class TileWorldEntity implements Collidable {
    private Rect shape;
    
    public TileWorldEntity(Rect shape) {
        this.shape = shape;
    }
    
    @Override
    public Rect handleCollisions(float ticks, Collection<RectCollision> collisions) {
        return shape;
    }
    
    @Override
    public Rect rehandleCollisions(float ticks, Collection<RectCollision> collisions) {
        return shape;
    }
    
    @Override
    public Vector2f getPosition(float millis) {
        return shape.getPosition();
    }
    
    @Override
    public Rect getRect(float millis) {
        return shape;
    }
    
}
