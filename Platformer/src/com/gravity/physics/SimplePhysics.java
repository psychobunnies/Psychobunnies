package com.gravity.physics;

import org.newdawn.slick.geom.Vector2f;

public class SimplePhysics implements Physics {
    private CollisionEngine collisionEngine;
    
    public SimplePhysics(CollisionEngine collisionEngine) {
        this.collisionEngine = collisionEngine;
    }
    
    @Override
    public PhysicalState computePhysics(Entity entity, PhysicalState state, float ticks) {
        return state.fastForward(ticks);
    }
    
    @Override
    public PhysicalState handleCollision(Entity entity, PhysicalState state, Collision[] collisions) {
        return new PhysicalState(state.getShape(), state.getPosition(), new Vector2f(0, 0));
    }
    
    @Override
    public PhysicalState rehandleCollision(Entity entity, PhysicalState state, Collision[] collisions) {
        System.err.println("WARNING: Rehandling collision for entity " + entity + " at " + state + " stupidly");
        return new PhysicalState(state.getShape(), state.getPosition(), new Vector2f(0, 0));
    }
    
}
