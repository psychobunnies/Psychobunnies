package com.gravity.physics;

import java.util.List;

import com.gravity.entity.Entity;

/**
 * Represents a set of physics created at the start of the world. Methods should be stateless.
 * 
 * Examples for subclasses would include GravityPhysics, FloatingPhysics
 * 
 * @author xiao
 */
public interface Physics {
    public PhysicalState computePhysics(Entity entity, float millis);
    
    public PhysicalState handleCollision(Entity entity, float millis, List<RectCollision> collisions);
    
    public PhysicalState rehandleCollision(Entity entity, float millis, List<RectCollision> collisions);
}
