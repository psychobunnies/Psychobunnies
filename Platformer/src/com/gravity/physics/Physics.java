package com.gravity.physics;

import com.gravity.entity.Entity;

/**
 * Represents a set of physics created at the start of the world. Methods should be stateless.
 * 
 * Examples for subclasses would include GravityPhysics, FloatingPhysics
 * 
 * @author xiao
 */
public interface Physics {
    /**
     * Compute the state of the entity according to this Physics after specified time, assuming nothing else in the world.
     * 
     * @return The PhysicalState of the object <i>AT TIME 0</i>
     */
    public PhysicalState computePhysics(Entity entity);
}
