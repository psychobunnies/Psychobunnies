package com.gravity.entity;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.physics.Collidable;
import com.gravity.physics.PhysicalState;

/**
 * Represents an object which can move in the world.
 * 
 * @author xiao
 */
public interface Entity extends Collidable {
    
    /**
     * @returns the current physical state of the entry
     */
    public PhysicalState getPhysicalState();
    
    /**
     * @returns the physical state of the entity a given number of milliseconds in the future.
     */
    public PhysicalState getPhysicalStateAt(float millis);
    
    /**
     * Sets a new physical state for the entity.
     */
    public void setPhysicalState(PhysicalState newState);
    
    /**
     * Informs the entity that an update has occurred.
     */
    public void updated(float millis);
    
    /**
     * @returns the velocity of the entity a the given time.
     */
    public Vector2f getVelocity(float millis);
}
