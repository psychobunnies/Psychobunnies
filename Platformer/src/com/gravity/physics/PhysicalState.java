package com.gravity.physics;

import org.newdawn.slick.geom.Vector2f;

import com.google.common.base.Preconditions;
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
    public final float surfaceVelX;
    private final Rect shape;

    public PhysicalState(Rect rect, float velX, float velY, float surfaceVelX) {
        this(rect, velX, velY, 0, 0, surfaceVelX);
    }

    public PhysicalState(Rect rect, float velX, float velY, float accX, float accY, float surfaceVelX) {
        Preconditions.checkArgument(rect != null, "Rect is null!");
        this.shape = rect;
        this.velX = velX;
        this.velY = velY;
        this.accX = accX;
        this.accY = accY;
        this.surfaceVelX = surfaceVelX;
    }

    public PhysicalState(Rect sha, Vector2f vel, float surfaceVelX) {
        this(sha, vel, new Vector2f(0, 0), surfaceVelX);
    }

    public PhysicalState(Rect sha, Vector2f vel, Vector2f acc, float surfaceVelX) {
        Preconditions.checkArgument(sha != null, "Rect is null!");
        velX = vel.x;
        velY = vel.y;
        accX = acc.x;
        accY = acc.y;
        shape = sha;
        this.surfaceVelX = surfaceVelX;
    }

    /**
     * Kill the movement of the object <br>
     * <b>Note</b> Also kills acceleration
     */
    public PhysicalState killMovement() {
        return new PhysicalState(shape, 0, 0, 0, 0, 0);
    }

    public PhysicalState teleport(float x, float y) {
        return new PhysicalState(shape.translateTo(x, y), 0, 0, 0, 0, 0);
    }

    /** Return the state of the object after specified time has passed. On negative values, "rewinds" the state backward in time */
    public PhysicalState snapshot(float millis) {
        //@formatter:off
        float newX = (velX + surfaceVelX) * millis + accX * millis * Math.abs(millis) / 2;
        float newY = velY * millis + accY * millis * Math.abs(millis) / 2;
        if (shape == null) {
            System.err.println("WTF??");
        }
        return new PhysicalState(shape.translate(newX, newY),
                                 velX + accX * millis,
                                 velY + accY * millis,
                                 accX,
                                 accY,
                                 surfaceVelX);
        //@formatter:on
    }

    /** Return the state of the object after specified time has passed. Then set acceleration to specified */
    public PhysicalState snapshotAndSetAccel(float millis, float newAccX, float newAccY) {
        //@formatter:off
        float newX = (velX + surfaceVelX) * millis + accX * millis * Math.abs(millis) / 2;
        float newY = velY * millis + accY * millis * Math.abs(millis) / 2;
        return new PhysicalState(shape.translate(newX, newY),
                                 velX + accX * millis,
                                 velY + accY * millis,
                                 newAccX,
                                 newAccY,
                                 surfaceVelX);
        //@formatter:on
    }

    public Vector2f getPosition() {
        return new Vector2f(shape.getX(), shape.getY());
    }

    public Vector2f getPositionAt(float millis) {
        //@formatter:off
        return new Vector2f(shape.getX() + (velX + surfaceVelX) * millis + accX * millis * Math.abs(millis) / 2, 
                            shape.getY() + velY * millis + accY * millis * Math.abs(millis) / 2);
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

    public PhysicalState setAcceleration(float newAccX, float newAccY) {
        return new PhysicalState(shape, velX, velY, newAccX, newAccY, surfaceVelX);
    }

    public PhysicalState setSurfaceSpeed(float surfVelX) {
        return new PhysicalState(shape, velX, velY, accX, accY, surfVelX);
    }

    public PhysicalState removeSurfaceSpeed() {
        return new PhysicalState(shape, velX - surfaceVelX, velY, accX, accY, 0f);
    }

    public PhysicalState addAcceleration(float addX, float addY) {
        return new PhysicalState(shape, velX, velY, accX + addX, accY + addY, surfaceVelX);
    }

    public PhysicalState setVelocity(float xVel, float yVel) {
        return new PhysicalState(shape, xVel, yVel, accX, accY, surfaceVelX);
    }

    public PhysicalState translate(float x, float y) {
        return new PhysicalState(shape.translate(x, y), velX, velY, accX, accY, surfaceVelX);
    }

    public Rect getRectangle() {
        // Rect is immutable
        return shape;
    }

    public Rect getRectangleAt(float millis) {
        Vector2f v = getPositionAt(millis);
        return shape.translateTo(v.x, v.y);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PhysicalState [velX=");
        builder.append(velX);
        builder.append(", velY=");
        builder.append(velY);
        builder.append(", accX=");
        builder.append(accX);
        builder.append(", accY=");
        builder.append(accY);
        builder.append(", surfaceVelX=");
        builder.append(surfaceVelX);
        builder.append(", shape=");
        builder.append(shape);
        builder.append("]");
        return builder.toString();
    }

    public float getSurfaceVelocityX() {
        return surfaceVelX;
    }

}
