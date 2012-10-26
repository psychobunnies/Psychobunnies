package com.gravity.physics;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

/**
 * This immutable class encapsulates an object's physical state in a world. Note: position should correspond to the centerX and centerY <br>
 * Feel free to add convenience methods as needed.
 * 
 * @author xiao
 */
public class PhysicalState {
    private final Vector2f position;
    private final Vector2f velocity;
    private final Shape shape;
    
    public PhysicalState(Shape sha, Vector2f pos, Vector2f vel) {
        position = pos;
        velocity = vel;
        shape = sha;
    }
    
    /** Return the state of the object after specified time as passed */
    public PhysicalState fastForward(float ticks) {
        float velX = velocity.x * ticks;
        float velY = velocity.y * ticks;
        Vector2f newpos = position.copy();
        newpos.x += velX;
        newpos.y += velY;
        Shape newsha = shape.transform(Transform.createTranslateTransform(velX, velY));
        return new PhysicalState(newsha, newpos, velocity);
    }
    
    public Vector2f getPosition() {
        return position;
    }
    
    public Vector2f getPositionAt(float ticks) {
        // Not very performant - maybe consider making this class store floats instead?
        return position.copy().add(velocity.copy().scale(ticks));
    }
    
    public Vector2f getVelocity() {
        return velocity;
    }
    
    public Shape getShape() {
        return shape;
    }
    
    @Override
    public String toString() {
        return "PhysicalState [position=" + position + ", velocity=" + velocity + ", shape=" + shape + "]";
    }
}
