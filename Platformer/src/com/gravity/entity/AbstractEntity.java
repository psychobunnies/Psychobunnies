package com.gravity.entity;

import com.gravity.map.CheckpointCollidable;
import com.gravity.map.LevelFinishZone;
import com.gravity.physics.Collidable;
import com.gravity.physics.PhysicalState;

public abstract class AbstractEntity implements Entity {

    protected PhysicalState state;

    public AbstractEntity(PhysicalState state) {
        this.state = state;
    }

    @Override
    public void setPhysicalState(PhysicalState newState) {
        state = newState;
    }

    @Override
    public PhysicalState getPhysicalStateAt(float millis) {
        return state.snapshot(millis);
    }

    @Override
    public PhysicalState getPhysicalState() {
        return state;
    }

    @Override
    public boolean causesCollisionsWith(Collidable other) {
        return !(other instanceof CheckpointCollidable || other instanceof LevelFinishZone || other instanceof TriggeredCollidable);
    }
}
