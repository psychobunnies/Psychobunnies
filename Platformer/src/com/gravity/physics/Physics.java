package com.gravity.physics;

/**
 * Represents a set of physics created at the start of the world. Methods should be stateless.
 * 
 * Examples for subclasses would include GravityPhysics, FloatingPhysics
 * 
 * @author xiao
 */
public interface Physics {
    public PhysicalState computePhysics(Entity entity, PhysicalState state, float ticks);
    
    public PhysicalState handleCollision(Entity entity, PhysicalState state, Collision[] collisions);
    
    public PhysicalState rehandleCollision(Entity entity, PhysicalState state, Collision[] collisions);
}
