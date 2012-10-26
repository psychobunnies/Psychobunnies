package com.gravity.physics;

public class GravityPhysics implements Physics {
    private CollisionEngine collisionEngine;
    
    public GravityPhysics(CollisionEngine collisionEngine) {
        this.collisionEngine = collisionEngine;
    }
    
    @Override
    public PhysicalState computePhysics(Entity entity, PhysicalState state, float ticks) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public PhysicalState handleCollision(Entity entity, PhysicalState state, Collision[] collisions) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public PhysicalState rehandleCollision(Entity entity, PhysicalState state, Collision[] collisions) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
