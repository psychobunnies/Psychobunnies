package com.gravity.entity;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import com.gravity.physics.PhysicalState;

public abstract class AbstractEntity implements Entity {
    
    protected PhysicalState state;
    
    public AbstractEntity(PhysicalState state) {
        this.state = state;
    }
    
    @Override
    public Vector2f getPosition(float millis) {
        return getPhysicalState(millis).getPosition();
    }
    
    @Override
    public Vector2f getVelocity(float millis) {
        return getPhysicalState(millis).getVelocity();
    }
    
    @Override
    public Shape getShape(float millis) {
        PhysicalState newState = getPhysicalState(millis);
        Shape shape = state.getShape();
        return shape.transform(Transform.createTranslateTransform(newState.posX - shape.getX(), newState.posY - shape.getY()));
    }
    
    @Override
    public void setPhysicalState(PhysicalState newState) {
        state = newState;
    }
    
}
