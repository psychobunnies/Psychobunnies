package com.gravity.physics;

import java.util.Collection;

import com.gravity.entity.Entity;

public class SimplePhysics implements Physics {
    
    SimplePhysics() {
        // No-op
    }
    
    @Override
    public PhysicalState computePhysics(Entity entity, float millis) {
        return entity.getPhysicalStateAt(millis);
    }
    
    @Override
    public PhysicalState handleCollision(Entity entity, float millis, Collection<RectCollision> collisions) {
        return entity.getPhysicalStateAt(millis).killMovement();
    }
    
    @Override
    public PhysicalState rehandleCollision(Entity entity, float millis, Collection<RectCollision> collisions) {
        PhysicalState state = entity.getPhysicalStateAt(millis);
        System.err.println("WARNING: Rehandling collision for entity " + entity + " at " + state + " stupidly");
        return state.killMovement();
    }
    
}