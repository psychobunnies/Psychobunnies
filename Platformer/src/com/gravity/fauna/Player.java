package com.gravity.fauna;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.entity.PhysicsEntity;
import com.gravity.geom.Rect;
import com.gravity.levels.GameplayControl;
import com.gravity.physics.GravityPhysics;
import com.gravity.physics.PhysicalState;
import com.gravity.root.GameSounds;

public class Player extends PhysicsEntity<GravityPhysics> {

    public static enum Movement {
        LEFT, RIGHT, STOP
    }

    public static int TOP_LEFT = 0, TOP_RIGHT = 1, BOT_RIGHT = 2, BOT_LEFT = 3;

    // PLAYER STARTING CONSTANTS (Units = pixels, milliseconds)
    private static final float JUMP_POWER = 0.48f; // .6 before reset
    private static final float MOVEMENT_INCREMENT = 1f / 8f;
    private static final Rect BASE_SHAPE = new Rect(0f, 0f, 15f, 32f);
    private static final Vector2f DEFAULT_VELOCITY = new Vector2f(0, 0);

    private final float MAX_SLING_STRENGTH = 0.75f; // 1 before reset
    private final float SLING_SPEED = 1f / 400f; // 500f before reset
    private final float JUMP_COOLDOWN = 50f;

    private final GameplayControl control;

    // GAME STATE STUFF
    private final String name;
    private Movement requested = Movement.STOP;
    private boolean jumpExecuted = false;
    private float jumpCooldown = JUMP_COOLDOWN;

    private boolean moveInProgress = false;
    public boolean slingshot;
    public float slingshotStrength = 0;

    public Player(GameplayControl control, GravityPhysics physics, String name, Vector2f startpos) {
        super(new PhysicalState(BASE_SHAPE.translate(startpos.x, startpos.y), DEFAULT_VELOCITY.copy(), 0f), physics);
        this.name = name;
        this.control = control;
    }

    public String getName() {
        return name;
    }

    public boolean isRunning() {
        return moveInProgress && !physics.entitiesHitOnGround(this).isEmpty();
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

    public void kill() {
        control.playerDies(this);
    }

    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////KEY-PRESS METHODS///////////////////////////////
    // //////////////////////////////////////////////////////////////////////////

    /**
     * @param jumping
     *            true if keydown, false if keyup
     */
    public void jump(boolean jumping) {
        if (jumping && !jumpExecuted && !physics.entitiesHitOnGround(this).isEmpty()) {
            jumpExecuted = true;
            GameSounds.playJumpSound(); // TODO: clean this up
            setPhysicalState(state.setVelocity(state.velX, state.velY - JUMP_POWER));
        }
    }

    /**
     * 
     * @param direction
     */
    public void move(Movement direction) {
        switch (direction) {
        case LEFT:
        case RIGHT: {
            requested = direction;
            break;
        }
        case STOP: {
            requested = direction;
            if (moveInProgress) {
                moveInProgress = false;
                float vel = state.velX;
                if (vel >= 0) {
                    setPhysicalState(state.setVelocity(Math.max(0, vel - MOVEMENT_INCREMENT), state.velY));
                } else {
                    setPhysicalState(state.setVelocity(Math.min(0, vel + MOVEMENT_INCREMENT), state.velY));
                }
            }
            break;
        }
        }
    }

    /**
     * 
     * @param pressed
     *            true if keydown, false if keyup
     */
    public void specialKey(boolean pressed) {
        if (pressed) {
            slingshot = true;
        } else {
            slingshot = false;
            control.specialMoveSlingshot(this, slingshotStrength);
        }
    }

    public void slingshotMe(float strength, Vector2f direction) {
        Vector2f velocity = direction.copy().normalise().scale(strength);
        state = state.setVelocity(velocity.x, velocity.y);
        moveInProgress = false;
    }

    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////ON-TICK METHODS/////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////

    @Override
    public void startUpdate(float millis) {
        super.startUpdate(millis);
        if (jumpExecuted) {
            jumpCooldown -= millis;
            if (jumpCooldown <= 0f) {
                jumpCooldown = JUMP_COOLDOWN;
                jumpExecuted = false;
            }
        }
    }

    @Override
    public void finishUpdate(float millis) {
        super.finishUpdate(millis);
        switch (requested) {
        case LEFT:
            moveInProgress = true;
            setPhysicalState(state.setVelocity(Math.min(state.velX, -MOVEMENT_INCREMENT), state.velY));
            break;
        case RIGHT:
            moveInProgress = true;
            setPhysicalState(state.setVelocity(Math.max(state.velX, MOVEMENT_INCREMENT), state.velY));
            break;
        default:
            // no-op
        }
        if (slingshot) {
            slingshotStrength += millis * SLING_SPEED;
            slingshotStrength = Math.min(slingshotStrength, MAX_SLING_STRENGTH);
        } else {
            slingshotStrength = 0;
        }
    }

    @Override
    public void unavoidableCollisionFound() {
        System.out.println("Player " + this.toString() + " was probably squashed by a moving platform.");
        kill();
    }
}
