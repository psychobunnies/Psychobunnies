package com.gravity.entity;

import java.util.List;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import com.gravity.physics.Collidable;
import com.gravity.physics.Collision;
import com.gravity.physics.PhysicalState;

/**
 * Represents an object which can move and is subject to collisions
 * 
 * @deprecated Use {@link Collidable} instead
 * @author xiao
 */
@Deprecated
public interface Entity {
    
    /**
     * Get the position of the Entity after specified time has passed
     * 
     * @param millis
     *            time since the last tick() call
     */
    public Vector2f getPosition(float millis);
    
    /**
     * Get the position of the Entity after specified time has passed
     * 
     * @param ticks
     *            time since the last tick() call
     */
    public Shape getShape(float millis);
    
    /**
     * Get the velocity of the Entity at the specified time
     * 
     * @param ticks
     *            time since the last tick() call
     */
    public Vector2f getVelocity(float millis);
    
    /**
     * Entity will collide with another entity - handle it.
     * 
     * @param collision
     *            an object containing info about the collision
     * @param millis
     *            the length of this timestep
     * @return the new position of the object at the full time, as specified by ticks
     */
    public PhysicalState handleCollisions(float millis, List<Collision> collisions);
    
    /**
     * Same as {@link Entity#handleCollisions(int, float)}, but may not change player's game state (health, etc) - useful for when handleCollision
     * proposes a new position which creates new collision problems.
     */
    public PhysicalState rehandleCollisions(float millis, List<Collision> collisions);
    
    /**
     * @returns the current physical state of the entry
     */
    public PhysicalState getCurrentPhysicalState();
    
    /**
     * @returns the physical state of the entity a given number of milliseconds in the future.
     */
    public PhysicalState getPhysicalState(float millis);
    
    /**
     * Sets a new physical state for the entity.
     */
    public void setPhysicalState(PhysicalState newState);
    
    /**
     * Informs the entity that an update has occurred.
     */
    public void updated(float millis);
}
