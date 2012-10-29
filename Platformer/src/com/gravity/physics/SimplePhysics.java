package com.gravity.physics;

import java.util.List;

import com.gravity.entity.Entity;

public class SimplePhysics implements Physics {
    
    SimplePhysics() {
        
    }
    
    @Override
    public PhysicalState computePhysics(Entity entity, float millis) {
        return entity.getPhysicalState(millis);
    }
    
    @Override
    public PhysicalState handleCollision(Entity entity, float millis, List<Collision> collisions) {
        return entity.getPhysicalState(millis).killMovement();
    }
    
    @Override
    public PhysicalState rehandleCollision(Entity entity, float millis, List<Collision> collisions) {
        PhysicalState state = entity.getPhysicalState(millis);
        System.err.println("WARNING: Rehandling collision for entity " + entity + " at " + state + " stupidly");
        return state.killMovement();
    }
    
}