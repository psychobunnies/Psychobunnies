package com.gravity.physics;

import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.gravity.entity.Entity;

public class GravityPhysics implements Physics {
    
    private final CollisionEngine collisionEngine;
    private final float gravity;
    private final float rehandleBackStep;
    
    GravityPhysics(CollisionEngine collisionEngine, float gravity, float rehandleBackStep) {
        Preconditions.checkArgument(rehandleBackStep < 0.0, "rehandleBackStep has to be negative, " + rehandleBackStep + " passed.");
        this.collisionEngine = collisionEngine;
        this.gravity = gravity;
        this.rehandleBackStep = rehandleBackStep;
    }
    
    public boolean isOnGround(Entity entity, float millis) {
        return collisionEngine.isOnGround(entity, millis);
    }
    
    @Override
    public PhysicalState computePhysics(Entity entity, float millis) {
        if (collisionEngine.isOnGround(entity, millis)) {
            return entity.getCurrentPhysicalState().snapshot(millis);
        } else {
            return entity.getCurrentPhysicalState().addAcceleration(0.0f, gravity).snapshot(millis).addAcceleration(0.0f, -gravity);
        }
    }
    
    @Override
    public PhysicalState handleCollision(Entity entity, float millis, List<Collision> collisions) {
        PhysicalState state = computePhysics(entity, millis);
        for (Collision c : collisions) {
            Entity them = c.getOtherEntity(entity);
            
            // HACK: assumes that a 4-sided Polygon will be a Rectangle
            if ((them.getShape(millis).getPointCount() == 4)) {
                state = resolveTerrainCollisions(getCollisionPoints(entity, collisions), state);
            } else {
                throw new RuntimeException("Cannot resolve non-Rectangle collision.");
            }
        }
        return state;
    }
    
    @Override
    public PhysicalState rehandleCollision(Entity entity, float millis, List<Collision> collisions) {
        PhysicalState oldState = entity.getCurrentPhysicalState();
        System.err.println("WARNING: Rehandling collisions for entity=" + entity.toString());
        if (collisionEngine.isOnGround(entity, 0f)) {
            return oldState.snapshot(rehandleBackStep);
        } else {
            return oldState.addAcceleration(0f, gravity).snapshot(rehandleBackStep).addAcceleration(0f, -gravity);
        }
    }
    
    /**
     * Get all collision points with terrain. Assumes there are up to 4 collision points.
     */
    private Entity[] getCollisionPoints(Entity entity, List<Collision> collisions) {
        Entity[] points = { null, null, null, null };
        for (Collision collision : collisions) {
            Set<Integer> colPoints = collision.getMyCollisions(entity);
            for (int point : colPoints) {
                points[point] = collision.getOtherEntity(entity);
            }
        }
        return points;
    }
    
    /**
     * Handles collision with terrain
     */
    private PhysicalState resolveTerrainCollisions(Entity[] points, PhysicalState state) {
        Entity etl = points[0];
        Entity etr = points[1];
        Entity ebr = points[2];
        Entity ebl = points[3];
        
        boolean tl = (etl != null);
        boolean tr = (etr != null);
        boolean br = (ebr != null);
        boolean bl = (ebl != null);
        int count = 0;
        
        float velX = state.velX, velY = state.velY;
        
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
                    velX = Math.max(state.velX, 0);
                    velY = Math.max(state.velY, 0);
                } else if (tr) {
                    // Hit top right
                    velX = Math.min(state.velX, 0);
                    velY = Math.max(state.velY, 0);
                } else if (br) {
                    // Hit bottom right
                    velX = Math.min(state.velX, 0);
                    velY = Math.min(state.velY, 0);
                } else if (bl) {
                    // Hit bottom left
                    velX = Math.max(state.velX, 0);
                    velY = Math.min(state.velY, 0);
                } else {
                    throw new RuntimeException("Should never hit this line: case 1");
                }
                break;
            case 2:
                if (tl && tr) {
                    // if you hit the ceiling
                    velY = 0;
                } else if (bl && br) {
                    // if you hit the floor
                    velY = 0;
                } else if (tr && br) {
                    // if you hit the right wall
                    velX = 0;
                } else if (tl && bl) {
                    // if you hit the left wall
                    velX = 0;
                } else {
                    // if you hit opposite corners
                    if ((points[0] == points[2] && points[0] != null) || (points[1] == points[3] && points[1] != null)) {
                        System.out.println("check opening size!!!");
                    }
                    velX = 0;
                    velY = 0;
                }
                break;
            default:
                // Collision on 2 or more sides
                velX = 0;
                velY = 0;
                break;
        }
        return state.setVelocity(velX, velY);
    }
    
}
