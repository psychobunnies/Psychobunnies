package com.gravity.physics;

import java.util.Collection;

import com.gravity.entity.PhysicallyStateful;

/**
 * Represents an object which is subject to collisions in the world
 * 
 * @author xiao
 */
public interface Collidable extends PhysicallyStateful {

    /**
     * Entity will collide with another entity - handle it, adjusting collidible's game state as necessary.
     * 
     * @param collection
     *            a list of collisions which occurred with this Collidable
     * @param millis
     *            the length of this timestep
     */
    public void handleCollisions(float millis, Collection<RectCollision> collection);

    /**
     * Same as {@link Collidable#handleCollisions(float, List<Collision>)}, but may not change collidible's game state (health, etc). Useful for when
     * handleCollision proposes a new position which creates new collision problems. Think of it as the handleCollision's exception handler.
     */
    public void rehandleCollisions(float millis, Collection<RectCollision> collisions);

    /**
     * Whether or not the other collidable should get collisions with this object. Should just be "return true;" in most cases.
     * 
     * Useful if this object is intangible in the game world. For example, help text should be informed of collisions by the players, but the players
     * themselves shouldn't necessarily know about it.
     */
    public boolean causesCollisionsWith(Collidable other);

}
