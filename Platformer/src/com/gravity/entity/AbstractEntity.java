package com.gravity.entity;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.geom.Rect;
import com.gravity.physics.PhysicalState;

public abstract class AbstractEntity implements Entity {
    
    protected PhysicalState state;
    
    public AbstractEntity(PhysicalState state) {
        this.state = state;
    }
    
    @Override
    public Vector2f getPosition(float millis) {
        return getPhysicalStateAt(millis).getPosition();
    }
    
    @Override
    public Rect getRect(float millis) {
        PhysicalState newState = getPhysicalStateAt(millis);
        return newState.getRectangle();
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
}
