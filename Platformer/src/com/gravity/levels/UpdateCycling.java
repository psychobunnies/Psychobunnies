package com.gravity.levels;

public interface UpdateCycling {
    
    /**
     * Informs the entity that an update has finished - advance time by the specified amount.
     * 
     * @param millis
     *            time since the last update cycle
     */
    public void finishUpdate(float millis);
    
    /**
     * Informs the entity that an update has begun - compute events within the Entity (e.g. Physics) for the specified time.
     * 
     * @param millis
     *            time since the last update cycle
     */
    public void startUpdate(float millis);
    
}