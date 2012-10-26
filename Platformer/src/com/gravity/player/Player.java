package com.gravity.player;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.gravity.gameplay.GravityGameController;
import com.gravity.map.GameWorld;
import com.gravity.physics.Collision;
import com.gravity.physics.CollisionEngine;
import com.gravity.physics.Entity;

public class Player implements Entity {
    
    public static enum Movement {
        LEFT, RIGHT, STOP, MISC
    }
    
    public static int TOP_LEFT = 0, TOP_RIGHT = 1, BOT_RIGHT = 2, BOT_LEFT = 3;
    
    // PLAYER STARTING CONSTANTS (Units = pixels, milliseconds)
    private final float JUMP_POWER = 0.6f;
    private final float MOVEMENT_INCREMENT = 1f / 8f;
    private final float MAX_VEL = 75f;
    @SuppressWarnings("unused")
    private final float VEL_DAMP = 0.5f;
    private final float GRAVITY = 1.0f / 750f;
    private final float MAX_SLING_STRENGTH = 1f;
    private final float SLING_SPEED = 1f / 500f;
    private final float FRICTION = 985f / 1000f;
    private final Shape BASE_SHAPE = new Rectangle(1f, 1f, 15f, 32f);
    
    // WORLD KNOWLEDGE
    private GameWorld map;
    private GravityGameController controller;
    
    // PLAYER CURRENT VALUES
    
    // TODO: bring these back into tile widths instead of pixel widths
    private Vector2f acceleration = new Vector2f(0, 0);
    private Vector2f position;
    private Vector2f velocity = new Vector2f(0, 0);
    private Shape myShape;
    public float slingshotStrength = 0;
    
    // GAME STATE STUFF
    private boolean onGround = false;
    public boolean slingshot = false;
    private final String name;
    private Movement requested = Movement.STOP;
    
    public Player(GameWorld map, String name, Vector2f startpos, GravityGameController c) {
        position = startpos;
        this.map = map;
        this.myShape = BASE_SHAPE;
        this.name = name;
        this.controller = c;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Player [name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }
    
    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////GET & SET METHODS///////////////////////////////
    // //////////////////////////////////////////////////////////////////////////
    public Vector2f getPosition() {
        return getPosition(0);
    }
    
    public void setPositionX(float x) {
        position.x = x;
    }
    
    @Override
    public Vector2f getPosition(int ticks) {
        return new Vector2f(position.x + (velocity.x * ticks), position.y + (velocity.y * ticks));
    }
    
    @Override
    public Shape getShape(int ticks) {
        return BASE_SHAPE.transform(Transform.createTranslateTransform(position.x + (velocity.x * ticks), position.y + (velocity.y * ticks)));
    }
    
    @Override
    public Vector2f getVelocity(int ticks) {
        return velocity.copy();
    }
    
    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////KEY-PRESS METHODS///////////////////////////////
    // //////////////////////////////////////////////////////////////////////////
    
    /**
     * @param jumping
     *            true if keydown, false if keyup
     */
    public void jump(boolean jumping) {
        if (jumping && onGround) {
            velocity.y -= JUMP_POWER;
        }
    }
    
    /**
     * 
     * @param direction
     */
    public void move(Movement direction) {
        switch (direction) {
            case LEFT: {
                if (Math.abs(velocity.x) < MOVEMENT_INCREMENT) {
                    velocity.x = -MOVEMENT_INCREMENT;
                }
                break;
            }
            case RIGHT: {
                if (Math.abs(velocity.x) < MOVEMENT_INCREMENT) {
                    velocity.x = MOVEMENT_INCREMENT;
                }
                break;
            }
            case STOP: {
                velocity.x = 0;
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
            controller.specialMoveSlingshot(this, slingshotStrength);
        }
    }
    
    // //////////////////////////////////////////////////////////////////////////
    // ///////////////////////////RESULTING ACTIONS//////////////////////////////
    // //////////////////////////////////////////////////////////////////////////
    
    public void slingshotMe(float strength, Vector2f direction) {
        velocity = direction.copy().normalise().scale(strength);
        slingshot = false;
    }
    
    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////COLLISION METHODS///////////////////////////////
    // //////////////////////////////////////////////////////////////////////////
    
    @Override
    public Shape handleCollisions(int millis, List<Collision> collisions) {
        for (Collision c : collisions) {
            Entity them = c.getOtherEntity(this);
            
            // HACK: assumes that a 4-sided Polygon will be a Rectangle
            if ((them.getShape(millis).getPointCount() == 4)) {
                resolveTerrainCollisions(getCollisionPoints(collisions), millis);
            } else {
                throw new RuntimeException("Cannot resolve non-Rectangle collision.");
            }
        }
        return myShape;
    }
    
    @Override
    public Shape rehandleCollisions(int ticks, List<Collision> collisions) {
        for (Collision c : collisions) {
            Entity them = c.getOtherEntity(this);
            
            // HACK: assumes that a 4-sided Polygon will be a Rectangle
            if ((them.getShape(ticks).getPointCount() == 4)) {
                resolveTerrainCollisions(getCollisionPoints(collisions), ticks);
            } else {
                throw new RuntimeException("Cannot resolve non-Rectangle collision.");
            }
        }
        return myShape;
    }
    
    /**
     * Get all collision points with terrain
     */
    private Entity[] getCollisionPoints(List<Collision> collisions) {
        Entity[] points = { null, null, null, null };
        for (Collision collision : collisions) {
            Set<Integer> colPoints = collision.getMyCollisions(this);
            for (int point : colPoints) {
                points[point] = collision.getOtherEntity(this);
            }
        }
        return points;
    }
    
    /**
     * Handles collision with terrain
     */
    private void resolveTerrainCollisions(Entity[] points, int millis) {
        Entity etl = points[0];
        Entity etr = points[1];
        Entity ebr = points[2];
        Entity ebl = points[3];
        
        boolean tl = (etl != null);
        boolean tr = (etr != null);
        boolean br = (ebr != null);
        boolean bl = (ebl != null);
        int count = 0;
        
        // Count the # of contact points
        for (Entity point : points) {
            if (point != null) {
                count++;
            }
        }
        // Decide what to do based on the # of contact points
        switch (count) {
            case 0:
                // No collisions
                throw new RuntimeException("handleCollisions should NOT be called with empty collision list");
            case 1:
                // If you only hit one corner, we will cancel velocity in the direction of the corner
                if (tl) {
                    // Hit top left
                    velocity.x = Math.max(velocity.x, 0);
                    velocity.y = Math.max(velocity.y, 0);
                } else if (tr) {
                    // Hit top right
                    velocity.x = Math.min(velocity.x, 0);
                    velocity.y = Math.max(velocity.y, 0);
                } else if (br) {
                    // Hit bottom right
                    velocity.x = Math.min(velocity.x, 0);
                    velocity.y = Math.min(velocity.y, 0);
                } else if (bl) {
                    // Hit bottom left
                    velocity.x = Math.max(velocity.x, 0);
                    velocity.y = Math.min(velocity.y, 0);
                } else {
                    throw new RuntimeException("Should never hit this line: case 1");
                }
                break;
            case 2:
                if (tl && tr) {
                    // if you hit the ceiling
                    velocity.y = 0;
                    onGround = false;
                } else if (bl && br) {
                    // if you hit the floor
                    velocity.y = 0;
                    onGround = true;
                } else if (tr && br) {
                    // if you hit the right wall
                    velocity.x = 0;
                } else if (tl && bl) {
                    // if you hit the left wall
                    velocity.x = 0;
                } else {
                    // if you hit opposite corners
                    if ((points[0] == points[2] && points[0] != null) || (points[1] == points[3] && points[1] != null)) {
                        System.out.println("check opening size!!!");
                    }
                    velocity.x = 0;
                    velocity.y = 0;
                }
                break;
            default:
                // Collision on 2 or more sides
                velocity.x = 0;
                velocity.y = 0;
                break;
        }
    }
    
    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////ON-TICK METHODS/////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////
    @Override
    public void tick(int millis) {
        updatePosition(millis);
        updateAcceleration(millis);
        updateVelocity(millis);
        updateSlingshot(millis);
        Shape hitbox = myShape.transform(Transform.createTranslateTransform(0, 5));
        List<Entity> collisions = map.getTouching(hitbox);
        onGround = false;
        for (Entity ent : collisions) {
            Map<Integer, List<Integer>> aCollisions = Maps.newHashMap(), bCollisions = Maps.newHashMap();
            Shape e = ent.getShape(0);
            CollisionEngine.getShapeIntersections(hitbox, e, aCollisions, bCollisions);
            Set<Integer> aPoints = Sets.newHashSet();
            Set<Integer> bPoints = Sets.newHashSet();
            CollisionEngine.getIntersectPoints(hitbox, e, aCollisions, aPoints, bPoints);
            CollisionEngine.getIntersectPoints(e, hitbox, bCollisions, bPoints, aPoints);
            if (!onGround && aPoints.contains(BOT_LEFT) && aPoints.contains(BOT_RIGHT)
                    || (aPoints.size() == 1 && (aPoints.contains(BOT_LEFT) || aPoints.contains(BOT_RIGHT)))) {
                onGround = true;
            }
            
            // terrain doesn't move so this is safe to be called. necessary for detecting when running over spikes
            ent.handleCollisions(millis, Lists.newArrayList(new Collision(ent, this, millis, bPoints, aPoints)));
        }
        isDead(millis);
    }
    
    public void updateAcceleration(float millis) {
        if (onGround) {
            acceleration.y = 0;
        } else {
            acceleration.y = GRAVITY;
        }
    }
    
    public void updateVelocity(float millis) {
        // dv = a
        velocity.add(acceleration.copy().scale(millis));
        
        // velocity < maxVel
        if (velocity.length() > MAX_VEL * millis) {
            velocity.scale(MAX_VEL * millis / velocity.length());
        }
        
        // implement friction
        if (onGround) {
            if (velocity.x > MOVEMENT_INCREMENT) {
                velocity.x = velocity.x * FRICTION;
            }
            
        }
    }
    
    public void updatePosition(float millis) {
        position.add(velocity.copy().scale(millis));
        updateShape();
    }
    
    public void updateSlingshot(float millis) {
        if (slingshot) {
            slingshotStrength += millis * SLING_SPEED;
            slingshotStrength = Math.min(slingshotStrength, MAX_SLING_STRENGTH);
        } else {
            slingshotStrength = 0;
        }
    }
    
    /**
     * Sets onGround depending on if the player is on the ground or not
     * 
     * 
     * /** CALL THIS EVERY TIME YOU DO ANYTHING TO POSITION OR SHAPE >>>>>>> 578f54515a017ccc7211c613d175bbac8740860c
     */
    private void updateShape() {
        myShape = BASE_SHAPE.transform(Transform.createTranslateTransform(position.x, position.y));
    }
    
    public void setRequestedMovement(Movement requested) {
        this.requested = requested;
    }
    
    /**
     * Checks to see if the player is dead
     */
    private void isDead(float millis) {
        // TODO
    }
}
