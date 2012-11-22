package com.gravity.entity;

import java.util.Collection;

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
    private float oldAccX, oldAccY;

    public PhysicsEntity(PhysicalState state, T physics) {
        super(state);
        this.physics = physics;
    }

    @Override
    public void handleCollisions(float millis, Collection<RectCollision> collisions) {
        state = physics.handleCollision(this, collisions);
    }

    @Override
    public void rehandleCollisions(float millis, Collection<RectCollision> collisions) {
        state = physics.rehandleCollision(this, collisions);
    }

    @Override
    public void startUpdate(float millis) {
        oldAccX = state.accX;
        oldAccY = state.accY;
        state = physics.computePhysics(this);
    }

    @Override
    public void finishUpdate(float millis) {
        if (state.accX != 0f) {
            System.out.println(state.toString());
        }
        state = state.snapshotAndSetAccel(millis, oldAccX, oldAccY);
    }
}
