package com.gravity.physics;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.gravity.entity.Entity;
import com.gravity.entity.PhysicallyStateful;
import com.gravity.geom.Rect;
import com.gravity.geom.Rect.Side;

/**
 * A Physics simulator which assumes gravity, but no bouncing.
 * 
 * @author xiao, predrag
 * 
 */
public class GravityPhysics implements Physics {

    private final CollisionEngine collisionEngine;
    private final float gravity;
    private final float backstep;
    private final float offsetGroundCheck;
    private final float allowedSideOverlap;
    private static final float EPS = 1e-6f;

    GravityPhysics(CollisionEngine collisionEngine, float gravity, float backstep, float offsetGroundCheck, float allowedSideOverlap) {
        Preconditions.checkArgument(backstep <= 0f, "Backstep has to be non-positive.");
        this.collisionEngine = collisionEngine;
        this.gravity = gravity;
        this.backstep = backstep;
        this.offsetGroundCheck = offsetGroundCheck;
        this.allowedSideOverlap = allowedSideOverlap; // e.g. if player overlaps tile by this much on bottom-and-side collision, ignore bottom and
                                                      // move player sideways to compensate
                                                      // Fixes: residual wall-hanging on corners of tiles
    }

    public boolean isOnGround(Entity entity) {
        Rect collider = entity.getPhysicalState().getRectangle().translate(0, offsetGroundCheck);
        List<Collidable> collisions = collisionEngine.collisionsInLayer(0f, collider, LayeredCollisionEngine.FLORA_LAYER);

        for (Collidable c : collisions) {
            c.handleCollisions(0f, Lists.newArrayList(new RectCollision(entity, c, 0f, null, null)));
        }

        return !collisions.isEmpty();
    }

    private boolean isRealBottomCollision(Collidable player, Collidable terrain, EnumSet<Side> playerCollisionSides) {
        if (playerCollisionSides.contains(Side.LEFT)) {
            return (player.getRect(0f).getX() + allowedSideOverlap) < terrain.getRect(0f).getMaxX();
        } else if (playerCollisionSides.contains(Side.RIGHT)) {
            return (player.getRect(0f).getMaxX() - allowedSideOverlap) > terrain.getRect(0f).getX();
        } else {
            return true;
        }
    }

    private float getFakeBottomCollisionCorrection(Collidable player, Collidable terrain, EnumSet<Side> playerCollisionSides) {
        if (playerCollisionSides.contains(Side.LEFT)) {
            return Math.max(0f, terrain.getRect(0f).getMaxX() - player.getRect(0f).getX() + EPS);
        } else if (playerCollisionSides.contains(Side.RIGHT)) {
            return Math.min(0f, terrain.getRect(0f).getX() - player.getRect(0f).getMaxX() - EPS);
        } else {
            return 0.0f;
        }
    }

    @Override
    public PhysicalState computePhysics(Entity entity) {
        if (isOnGround(entity)) {
            return entity.getPhysicalState();
        } else {
            return entity.getPhysicalState().addAcceleration(0f, gravity);
        }
    }

    @Override
    public PhysicalState handleCollision(Entity entity, Collection<RectCollision> collisions) {
        PhysicalState state = entity.getPhysicalState();
        float velX = state.velX;
        float velY = state.velY;
        float accX = state.accX;
        float accY = state.accY;
        float corrX = 0f;
        for (RectCollision c : collisions) {
            Collidable other = c.getOtherEntity(entity);
            if (!other.isPassThrough()) {
                EnumSet<Side> sides = c.getMyCollisions(entity);
                Preconditions.checkArgument(sides != null, "Collision passed did not involve entity: " + entity + ", " + c);

                if (Side.isSimpleSet(sides)) {
                    if (sides.contains(Side.TOP)) {
                        velY = Math.max(velY, 0);
                        accY = Math.max(accY, 0);
                    }
                    if (sides.contains(Side.LEFT)) {
                        velX = Math.max(velX, 0);
                        accX = Math.max(accX, 0);
                    }
                    if (sides.contains(Side.BOTTOM)) {
                        if (isRealBottomCollision(entity, other, sides)) {
                            velY = Math.min(velY, 0);
                            accY = Math.min(accY, 0);
                        } else {
                            corrX = getFakeBottomCollisionCorrection(entity, other, sides);
                        }
                    }
                    if (sides.contains(Side.RIGHT)) {
                        velX = Math.min(velX, 0);
                        accX = Math.min(accX, 0);
                    }
                } else {
                    velX = 0;
                    velY = 0;
                    accX = 0;
                    accY = 0;
                }
            }
        }
        Rect r = entity.getRect(0f);
        if (corrX != 0.0f) {
            r.translate(corrX, 0f);
        }
        return new PhysicalState(r, velX, velY, accX, accY);
    }

    @Override
    public PhysicalState rehandleCollision(PhysicallyStateful entity, Collection<RectCollision> collisions) {
        System.err.println("Warning: rehandling collisions for: " + entity);
        return entity.getPhysicalState().snapshot(backstep);
    }
}
