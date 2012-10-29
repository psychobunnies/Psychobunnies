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
    
    public final float posX, posY, velX, velY, accX, accY;
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
    
    /** Return the state of the object after specified time has passed. On negative values, "rewinds" the state backward in time */
    public PhysicalState snapshot(float millis) {
        //@formatter:off
        return new PhysicalState(shape,
                                 posX + velX * millis + accX * millis * Math.abs(millis) / 2, 
                                 posY + velY * millis + accY * millis * Math.abs(millis) / 2,
                                 velX + accX * millis,
                                 velY + accY * millis,
                                 accX,
                                 accY);
        //@formatter:on
    }
    
    public Vector2f getPosition() {
        return new Vector2f(posX, posY);
    }
    
    public Vector2f getPositionAt(float millis) {
        //@formatter:off
        return new Vector2f(posX + velX * millis + accX * millis * millis / 2, 
                            posY + velY * millis + accY * millis * millis / 2);
        //@formatter:on
    }
    
    public Vector2f getVelocity() {
        return new Vector2f(velX, velY);
    }
    
    public Vector2f getAcceleration() {
        return new Vector2f(accX, accY);
    }
    
    public PhysicalState addAcceleration(float addX, float addY) {
        return new PhysicalState(shape, posX, posY, velX, velY, accX + addX, accY + addY);
    }
    
    public PhysicalState setVelocity(float xVel, float yVel) {
        return new PhysicalState(shape, posX, posY, xVel, yVel, accX, accY);
    }
    
    public Shape getShape() {
        return shape;
    }
    
    @Override
    public String toString() {
        return "PhysicalState [pos= (" + posX + "," + posY + "), vel= (" + velX + "," + velY + "), acc= (" + accX + "," + accY + "), shape=" + shape + "]";
    }
}
