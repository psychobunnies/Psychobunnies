package com.gravity.physics;

import java.util.Collection;
import java.util.EnumSet;

import com.google.common.base.Preconditions;
import com.gravity.entity.Entity;
import com.gravity.geom.Rect;
import com.gravity.geom.Rect.Side;

public class StopCollisionStrategy implements CollisionStrategy {
    private static final float EPS = 1e-6f;

    private final float allowedSideOverlap;

    public StopCollisionStrategy(float allowedSideOverlap) {
        this.allowedSideOverlap = allowedSideOverlap; // e.g. if player overlaps tile by this much on bottom-and-side collision, ignore bottom and
                                                      // move player sideways to compensate
                                                      // Fixes: residual wall-hanging on corners of tiles
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
    public PhysicalState handleCollision(Entity entity, Collection<RectCollision> collisions) {
        PhysicalState state = entity.getPhysicalState();
        float velX = state.velX;
        float velY = state.velY;
        float accX = state.accX;
        float accY = state.accY;
        float corrX = 0f;
        float corrY = Float.POSITIVE_INFINITY;
        for (RectCollision c : collisions) {
            Collidable other = c.getOtherEntity(entity);
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
                        corrY = Math.max(0f, Math.min(corrY, other.getRect(0f).getY() - entity.getRect(0f).getMaxY() - EPS));
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
        Rect r = entity.getRect(0f);
        if (corrX != 0.0f) {
            r = r.translate(corrX, 0f);
        }
        if (!Float.isInfinite(corrY)) {
            r = r.translate(0f, corrY);
        }
        return new PhysicalState(r, velX, velY, accX, accY);
    }

}
