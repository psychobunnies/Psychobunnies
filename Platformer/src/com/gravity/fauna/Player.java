package com.gravity.fauna;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.entity.PhysicsEntity;
import com.gravity.entity.TriggeredCollidable;
import com.gravity.geom.Rect;
import com.gravity.levels.GameplayControl;
import com.gravity.map.CheckpointCollidable;
import com.gravity.map.LevelFinishZone;
import com.gravity.physics.Collidable;
import com.gravity.physics.GravityPhysics;
import com.gravity.physics.PhysicalState;
import com.gravity.root.GameSounds;
import com.gravity.root.GameSounds.Event;

public class Player extends PhysicsEntity<GravityPhysics> {

    public static enum Movement {
        LEFT, RIGHT, STOP
    }

    public static int TOP_LEFT = 0, TOP_RIGHT = 1, BOT_RIGHT = 2, BOT_LEFT = 3;

    // PLAYER STARTING CONSTANTS (Units = pixels, milliseconds)
    private static final float JUMP_POWER = 0.48f; // .6 before reset
    private static final float MOVEMENT_INCREMENT = 1f / 8f;
    private static final Vector2f DEFAULT_VELOCITY = new Vector2f(0, 0);
    private static final float SLINGSHOT_COOLDOWN = 700f;

    private final float MAX_SLING_STRENGTH = 0.75f; // 1 before reset
    private final float JUMP_COOLDOWN = 50f;

    public static final Rect BASE_SHAPE = new Rect(6f, 0f, 20f, 48f);

    private final GameplayControl control;

    // GAME STATE STUFF
    private final String name;
    private Movement requested = Movement.STOP;
    private boolean jumpExecuted = false;
    private float jumpCooldown = JUMP_COOLDOWN;

    private boolean moveInProgress = false;
    public boolean slingshot;
    public float slingshotCooldown = 0f;
    public float slingshotStrength = 0;
    private boolean lastWalkedRight = true;

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

    public boolean isRising() {
        return physics.entitiesHitOnGround(this).isEmpty() && state.velY < 0;
    }

    public boolean isFalling() {
        return physics.entitiesHitOnGround(this).isEmpty() && state.velY >= 0;
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
            GameSounds.playSoundFor(Event.JUMP);
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
        if (pressed && slingshotCooldown <= 0) {
            slingshotCooldown = SLINGSHOT_COOLDOWN;
            slingshot = true;
        } else if (slingshot) {
            slingshot = false;
            control.specialMoveSlingshot(this, slingshotStrength);
        } else if (pressed==false) {
            GameSounds.playSoundFor(Event.NO_SLING);
        }
    }

    public void slingshotMe(float strength, Vector2f direction) {
        GameSounds.playSoundFor(Event.WHEE);
        Vector2f velocity = direction.copy().normalise().scale(strength);
        float velX = velocity.x, velY = velocity.y;
        if (state.velX > 0 && velX > 0) {
            velX = Math.max(state.velX, velX);
        } else if (state.velX < 0 && velX < 0) {
            velX = Math.min(state.velX, velX);
        }
        if (state.velY > 0 && velY > 0) {
            velY = Math.max(state.velY, velY);
        } else if (state.velY < 0 && velY < 0) {
            velY = Math.min(state.velY, velY);
        }
        state = state.setVelocity(velX, velY);
        moveInProgress = false;
    }

    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////ON-TICK METHODS/////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////

    @Override
    public void startUpdate(float millis) {
        super.startUpdate(millis);

        if (slingshotCooldown > 0) {
            slingshotCooldown -= millis;
        }
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
            if (physics.entitiesHitLeft(this).isEmpty()) {
                moveInProgress = true;
                setPhysicalState(state.setVelocity(Math.min(state.velX, -MOVEMENT_INCREMENT), state.velY));
            } else {
                setPhysicalState(state.setVelocity(0, state.velY));
            }
            break;
        case RIGHT:
            if (physics.entitiesHitRight(this).isEmpty()) {
                moveInProgress = true;
                setPhysicalState(state.setVelocity(Math.max(state.velX, MOVEMENT_INCREMENT), state.velY));
            } else {
                setPhysicalState(state.setVelocity(0, state.velY));
            }
            break;
        default:
            // no-op
        }

        if (state.getVelocity().x < 0) {
            lastWalkedRight = false;
        } else if (state.getVelocity().x > 0) {
            lastWalkedRight = true;
        } else {
            moveInProgress = false;
        }

        if (slingshot) {
            slingshotStrength = MAX_SLING_STRENGTH;
        } else {
            slingshotStrength = 0;
        }
    }

    @Override
    public void unavoidableCollisionFound() {
        System.out.println("Player " + this.toString() + " was probably squashed by a moving platform.");
        GameSounds.playSoundFor(Event.CRUSHED);
        kill();
    }

    public boolean getLastWalkedRight() {
        return lastWalkedRight;
    }

    @Override
    public boolean causesCollisionsWith(Collidable other) {
        return !(other instanceof CheckpointCollidable || other instanceof LevelFinishZone || other instanceof TriggeredCollidable || other instanceof Player);
    }
}
