package com.gravity.physics;

import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.geom.Rect;

/**
 * Represents an object which is subject to collisions in the world
 * 
 * @author xiao
 */
public interface Collidable {

    /**
     * Get the position of the Entity after specified time has passed
     * 
     * @param millis
     *            time since the last tick() call
     */
    public Vector2f getPosition(float millis);

    /**
     * Get the rectangle of the Entity after specified time has passed
     * 
     * @param ticks
     *            time since the last tick() call
     */
    public Rect getRect(float millis);

    /**
     * Entity will collide with another entity - handle it, adjusting collidible's game state as necessary.
     * 
     * @param collection
     *            a list of collisions which occured with this Collidible
     * @param millis
     *            the length of this timestep
     */
    public void handleCollisions(float millis, Collection<RectCollision> collection);

    /**
     * Same as {@link Collidible#handleCollisions(float, List<Collision>)}, but may not change collidible's game state (health, etc). Useful for when
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
