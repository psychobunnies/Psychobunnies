package com.gravity.fauna;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.entity.PhysicsEntity;
import com.gravity.geom.Rect;
import com.gravity.physics.GravityPhysics;
import com.gravity.physics.PhysicalState;

public class Player extends PhysicsEntity<GravityPhysics> {
    
    public static enum Movement {
        LEFT, RIGHT, STOP
    }
    
    public static int TOP_LEFT = 0, TOP_RIGHT = 1, BOT_RIGHT = 2, BOT_LEFT = 3;
    
    // PLAYER STARTING CONSTANTS (Units = pixels, milliseconds)
    private static final float JUMP_POWER = 0.5f;
    private static final float MOVEMENT_INCREMENT = 1f / 8f;
    private static final Rect BASE_SHAPE = new Rect(0f, 0f, 15f, 32f);
    private static final Vector2f DEFAULT_VELOCITY = new Vector2f(0, 0);
    
    // GAME STATE STUFF
    private final String name;
    private Movement requested = Movement.STOP;
    
    public Player(GravityPhysics physics, String name, Vector2f startpos) {
        super(new PhysicalState(BASE_SHAPE.translate(startpos.x, startpos.y), DEFAULT_VELOCITY.copy()), physics);
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Player [name=");
        builder.append(name);
        builder.append(", state=");
        builder.append(state);
        builder.append("]");
        return builder.toString();
    }
    
    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////KEY-PRESS METHODS///////////////////////////////
    // //////////////////////////////////////////////////////////////////////////
    
    /**
     * @param jumping
     *            true if keydown, false if keyup
     */
    public void jump(boolean jumping) {
        if (jumping && physics.isOnGround(state, 0)) {
            setPhysicalState(state.setVelocity(state.velX, state.velY - JUMP_POWER));
        }
    }
    
    /**
     * 
     * @param direction
     */
    public void move(Movement direction) {
        switch (direction) {
            case LEFT: {
                requested = direction;
                break;
            }
            case RIGHT: {
                requested = direction;
                break;
            }
            case STOP: {
                requested = direction;
                setPhysicalState(state.setVelocity(0f, state.velY));
                break;
            }
        }
    }
    
    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////ON-TICK METHODS/////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////
    
    @Override
    public void finishUpdate(float millis) {
        super.finishUpdate(millis);
        switch (requested) {
            case LEFT:
                setPhysicalState(state.setVelocity(-MOVEMENT_INCREMENT, state.velY));
                break;
            case RIGHT:
                setPhysicalState(state.setVelocity(MOVEMENT_INCREMENT, state.velY));
                break;
            default:
                // no-op
        }
    }
    
}
