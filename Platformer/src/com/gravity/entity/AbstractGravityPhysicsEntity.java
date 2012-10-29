package com.gravity.entity;

import java.util.List;

import com.gravity.physics.Collision;
import com.gravity.physics.GravityPhysics;
import com.gravity.physics.PhysicalState;

public abstract class AbstractGravityPhysicsEntity extends AbstractEntity {
    
    protected final GravityPhysics physics;
    
    public AbstractGravityPhysicsEntity(PhysicalState state, GravityPhysics physics) {
        super(state);
        this.physics = physics;
    }
    
    @Override
    public PhysicalState handleCollisions(float millis, List<Collision> collisions) {
        return physics.handleCollision(this, millis, collisions);
    }
    
    @Override
    public PhysicalState rehandleCollisions(float millis, List<Collision> collisions) {
        return physics.rehandleCollision(this, millis, collisions);
    }
    
    @Override
    public PhysicalState getCurrentPhysicalState() {
        return state;
    }
    
    @Override
    public PhysicalState getPhysicalState(float millis) {
        return physics.computePhysics(this, millis);
    }
    
}
