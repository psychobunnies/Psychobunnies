package com.gravity.physics;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

/**
 * This immutable class encapsulates an object's physical state in a world. <br>
 * Note: position should correspond to the centerX and centerY <br>
 * Feel free to add convenience methods as needed.
 * 
 * @author xiao
 */
public class PhysicalState {
    private final float posX, posY, velX, velY, accX, accY;
    private final Shape shape;
    
    public PhysicalState(Shape sha, float posX, float posY, float velX, float velY) {
        this(sha, posX, posY, velX, velY, 0, 0);
    }
    
    public PhysicalState(Shape sha, float posX, float posY, float velX, float velY, float accX, float accY) {
        this.shape = sha;
        this.posX = posX;
        this.posY = posY;
        this.velX = velX;
        this.velY = velY;
        this.accX = accX;
        this.accY = accY;
    }
    
    public PhysicalState(Shape sha, Vector2f pos, Vector2f vel) {
        this(sha, pos, vel, new Vector2f(0, 0));
    }
    
    public PhysicalState(Shape sha, Vector2f pos, Vector2f vel, Vector2f acc) {
        posX = pos.x;
        posY = pos.y;
        velX = vel.x;
        velY = vel.y;
        accX = acc.x;
        accY = acc.y;
        shape = sha;
    }
    
    /**
     * Kill the movement of the object <br>
     * <b>Note</b> Also kills acceleration
     */
    public PhysicalState killMovement() {
        return new PhysicalState(shape, posX, posY, 0, 0, 0, 0);
    }
    
    /** Return the state of the object after specified time as passed */
    public PhysicalState fastForward(float ticks) {
        //@formatter:off
        return new PhysicalState(shape,
                                 posX + velX * ticks + accX * ticks * ticks / 2, 
                                 posY + velY * ticks + accY * ticks * ticks / 2,
                                 velX + accX * ticks,
                                 velY + accY * ticks,
                                 accX,
                                 accY);
        //@formatter:on
    }
    
    public Vector2f getPosition() {
        return new Vector2f(posX, posY);
    }
    
    public Vector2f getPositionAt(float ticks) {
        // Not very performant - maybe consider making this class store floats instead?
        //@formatter:off
        return new Vector2f(posX + velX * ticks + accX * ticks * ticks / 2, 
                            posY + velY * ticks + accY * ticks * ticks / 2);
        //@formatter:on
    }
    
    public Vector2f getVelocity() {
        return new Vector2f(velX, velY);
    }
    
    public Vector2f getAcceleration() {
        return new Vector2f(accX, accY);
    }
    
    public Shape getShape() {
        return shape;
    }
    
    @Override
    public String toString() {
        return "PhysicalState [pos= (" + posX + "," + posY + "), vel= (" + velX + "," + velY + "), acc= (" + accX + "," + accY + "), shape=" + shape
                + "]";
    }
}
