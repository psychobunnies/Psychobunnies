package com.gravity.entity;

import java.util.List;

import org.newdawn.slick.geom.Shape;

import com.gravity.physics.Collision;
import com.gravity.physics.PhysicalState;

/**
 * Represents a convex shape in map terrain for collision detection.
 * 
 * @author xiao
 */
public class TileWorldEntity extends AbstractEntity {
    
    public TileWorldEntity(Shape shape) {
        super(new PhysicalState(shape, 0f, 0f, 0f, 0f));
    }
    
    @Override
    public PhysicalState handleCollisions(float ticks, List<Collision> collisions) {
        return state;
    }
    
    @Override
    public PhysicalState rehandleCollisions(float ticks, List<Collision> collisions) {
        return state;
    }
    
    @Override
    public PhysicalState getPhysicalState(float millis) {
        return state;
    }
    
    @Override
    public PhysicalState getCurrentPhysicalState() {
        return state;
    }
    
    @Override
    public void updated(float millis) {
        // no-op
    }
    
}
