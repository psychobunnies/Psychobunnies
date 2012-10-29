package com.gravity.physics;

import com.gravity.entity.Entity;

public interface CollisionEngine {
    
    public boolean addEntity(Entity entity);
    
    public boolean removeEntity(Entity entity);
    
    public boolean isOnGround(Entity entity, float millis);
    
    public void update(float millis);
    
}