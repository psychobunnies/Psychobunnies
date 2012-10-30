package com.gravity.physics;

import java.util.Collection;

import com.gravity.entity.Entity;
import com.gravity.entity.PhysicallyStateful;

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
    public PhysicalState computePhysics(PhysicallyStateful entity, float millis);
    
    /**
     * Bounce, stop, or otherwise handle the physics of collisions.
     * 
     * @param entity
     *            The entity being collided.
     * @param millis
     *            The time of the collision.
     * @param collisions
     *            The collisions the entity participated in.
     * @return The updated state of the object <i>AT TIME 0</i>
     */
    public PhysicalState handleCollision(Entity entity, float millis, Collection<RectCollision> collisions);
    
    public PhysicalState rehandleCollision(PhysicallyStateful entity, float millis, Collection<RectCollision> collisions);
}
