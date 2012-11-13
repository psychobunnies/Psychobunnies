package com.gravity.entity;

import com.gravity.physics.PhysicalState;
import com.gravity.physics.Physics;

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
    public void startUpdate(float millis) {
        oldAccX = state.accX;
        oldAccY = state.accY;
        state = physics.computePhysics(this);
    }

    @Override
    public void finishUpdate(float millis) {
        state = state.snapshotAndSetAccel(millis, oldAccX, oldAccY);
    }
}
