package com.gravity.physics;


public abstract class AbstractCollidable implements Collidable {

    private PhysicalState state;

    public AbstractCollidable(PhysicalState state) {
        this.state = state;
    }

    @Override
    public PhysicalState getPhysicalState() {
        return state;
    }

    @Override
    public PhysicalState getPhysicalStateAt(float millis) {
        return state.snapshot(millis);
    }

    @Override
    public void setPhysicalState(PhysicalState newState) {
        state = newState;
    }

}
