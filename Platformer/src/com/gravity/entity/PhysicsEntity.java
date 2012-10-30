package com.gravity.entity;

import java.util.Collection;

import com.gravity.geom.Rect;
import com.gravity.physics.PhysicalState;
import com.gravity.physics.Physics;
import com.gravity.physics.RectCollision;

/**
 * A collidable entity which obeys a set of physics.
 * 
 * @author xiao, predrag
 */
public abstract class PhysicsEntity<T extends Physics> extends AbstractEntity {
    
    protected final T physics;
    
    public PhysicsEntity(PhysicalState state, T physics) {
        super(state);
        this.physics = physics;
    }
    
    @Override
    public Rect handleCollisions(float millis, Collection<RectCollision> collisions) {
        return physics.handleCollision(this, millis, collisions).getRectangleAt(millis);
    }
    
    @Override
    public Rect rehandleCollisions(float millis, Collection<RectCollision> collisions) {
        return physics.rehandleCollision(this, millis, collisions).getRectangleAt(millis);
    }
    
    @Override
    public void startUpdate(float millis) {
        state = physics.computePhysics(this, millis);
    }
    
    @Override
    public void finishUpdate(float millis) {
        state = state.snapshot(millis);
    }
}
