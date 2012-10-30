package com.gravity.physics;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.geom.Rect;

/**
 * This immutable class encapsulates an object's physical state in a world. <br>
 * Note: position should correspond to the centerX and centerY <br>
 * Feel free to add convenience methods as needed.
 * 
 * @author xiao
 */
public class PhysicalState {
    
    public final float velX, velY, accX, accY;
    private final Rect shape;
    
    private final float EPS = 1e-6f;
    
    public PhysicalState(Rect rect, float velX, float velY) {
        this(rect, velX, velY, 0, 0);
    }
    
    public PhysicalState(Rect rect, float velX, float velY, float accX, float accY) {
        this.shape = rect;
        this.velX = velX;
        this.velY = velY;
        this.accX = accX;
        this.accY = accY;
    }
    
    public PhysicalState(Rect sha, Vector2f vel) {
        this(sha, vel, new Vector2f(0, 0));
    }
    
    public PhysicalState(Rect sha, Vector2f vel, Vector2f acc) {
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
        return new PhysicalState(shape, 0, 0, 0, 0);
    }
    
    /** Return the state of the object after specified time has passed. On negative values, "rewinds" the state backward in time */
    public PhysicalState snapshot(float millis) {
        //@formatter:off
        float newX = velX * millis + accX * millis * Math.abs(millis) / 2;
        float newY = velY * millis + accY * millis * Math.abs(millis) / 2;
        return new PhysicalState(shape.translate(newX, newY),
                                 velX + accX * millis,
                                 velY + accY * millis,
                                 accX,
                                 accY);
        //@formatter:on
    }
    
    /** Return the state of the object after specified time has passed. For this compute ONLY, add specified acceleration to state's acceleration */
    public PhysicalState snapshot(float millis, float addAccX, float addAccY) {
        float aX = accX + addAccX;
        float aY = accY + addAccY;
        //@formatter:off
        float newX = velX * millis + aX * millis * Math.abs(millis) / 2;
        float newY = velY * millis + aY * millis * Math.abs(millis) / 2;
        return new PhysicalState(shape.translate(newX, newY),
                                 velX + aX * millis,
                                 velY + aY * millis,
                                 accX,
                                 accY);
        //@formatter:on
    }
    
    public Vector2f getPosition() {
        return new Vector2f(shape.getX(), shape.getY());
    }
    
    public Vector2f getPositionAt(float millis) {
        //@formatter:off
        return new Vector2f(shape.getX() + velX * millis + accX * millis * millis / 2, 
                            shape.getY() + velY * millis + accY * millis * millis / 2);
        //@formatter:on
    }
    
    public Vector2f getVelocity() {
        return new Vector2f(velX, velY);
    }
    
    public Vector2f getVelocityAt(float millis) {
        return new Vector2f(velX + millis * accX, velY + millis * accY);
    }
    
    public Vector2f getAcceleration() {
        return new Vector2f(accX, accY);
    }
    
    public PhysicalState addAcceleration(float addX, float addY) {
        return new PhysicalState(shape, velX, velY, accX + addX, accY + addY);
    }
    
    public PhysicalState setVelocity(float xVel, float yVel) {
        return new PhysicalState(shape, xVel, yVel, accX, accY);
    }
    
    public PhysicalState translate(float x, float y) {
        return new PhysicalState(shape.translate(x, y), velX, velY, accX, accY);
    }
    
    public Rect getRectangle() {
        return new Rect(shape);
    }
    
    public Rect getRectangleAt(float millis) {
        return shape.translate(getPositionAt(millis));
    }
    
    @Override
    public String toString() {
        return "PhysicalState [rect=" + shape + ", vel= (" + velX + "," + velY + "), acc= (" + accX + "," + accY + ")]";
    }
}
