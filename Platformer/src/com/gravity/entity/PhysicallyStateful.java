package com.gravity.entity;

import com.gravity.physics.PhysicalState;

public interface PhysicallyStateful {
    
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
    
}