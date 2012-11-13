package com.gravity.physics;

import java.util.Collection;

import com.gravity.entity.Entity;

public interface CollisionStrategy {
    /**
     * Bounce, stop, or otherwise handle the physics of collisions.
     * 
     * @param entity
     *            The entity being collided.
     * @param millis
     *            The time since the last update() call
     * @param collisions
     *            The collisions the entity participated in.
     * @return The updated state of the object <i>AT TIME 0</i>
     */
    public PhysicalState handleCollision(Entity entity, Collection<RectCollision> collisions);

}
