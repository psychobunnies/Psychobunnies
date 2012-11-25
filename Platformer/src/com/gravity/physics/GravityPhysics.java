package com.gravity.physics;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.gravity.entity.Entity;
import com.gravity.entity.PhysicallyStateful;
import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.geom.Rect.Side;
import com.gravity.map.tiles.BouncyTile;

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
    private final float groundFriction;
    private final float frictionStopCutoff; // the velocity below which friction makes you stop completely
    private final float frictionAccelRatio; // the maximum fraction of your velocity that friction acceleration may represent
    private static final float EPS = 1e-6f;

    GravityPhysics(CollisionEngine collisionEngine, float gravity, float backstep, float offsetGroundCheck, float allowedSideOverlap,
            float groundFriction, float frictionStopCutoff, float frictionAccelRatio) {
        Preconditions.checkArgument(backstep <= 0f, "Backstep has to be non-positive.");
        this.collisionEngine = collisionEngine;
        this.gravity = gravity;
        this.backstep = backstep;
        this.offsetGroundCheck = offsetGroundCheck;
        this.groundFriction = groundFriction;
        this.frictionStopCutoff = frictionStopCutoff;
        this.frictionAccelRatio = frictionAccelRatio;
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

        for (Collidable c : collisions) {
            if (c.causesCollisionsWith(entity)) {
                return true;
            }
        }
        return false;
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
            PhysicalState state = entity.getPhysicalState();
            if (Math.abs(state.velX) <= frictionStopCutoff) {
                state = state.setVelocity(0f, state.velY);
            } else {
                state = state.addAcceleration(-Math.signum(state.velX) * Math.min(groundFriction, Math.abs(state.velX) / frictionAccelRatio), 0);
            }
            return state;
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
        float corrY = Float.POSITIVE_INFINITY;
        Rect possiblePos = new Rect(-100000000.0f, -100000000.0f,
                                    200000000.0f, 200000000.0f);
        for (RectCollision c : collisions) {
            Collidable other = c.getOtherEntity(entity);
            Rect otherRect = other.getRect(0);
            EnumSet<Side> sides = c.getMyCollisions(entity);
            Preconditions.checkArgument(sides != null, "Collision passed did not involve entity: " + entity + ", " + c);

            if (Side.isSimpleSet(sides)) {
                if (sides.contains(Side.TOP)) {
                    if (other instanceof BouncyTile) {
                        velY = Math.abs(velY);
                        accY = Math.max(accY, 0);
                    } else {
                        velY = Math.max(velY, 0);
                        accY = Math.max(accY, 0);
                    }
                }
                if (sides.contains(Side.LEFT)) {
                    if (other instanceof BouncyTile) {
                        velX = Math.abs(velX);
                        accX = Math.max(accX, 0);
                    } else {
                        velX = Math.max(velX, 0);
                        accX = Math.max(accX, 0);
                    }
                }
                if (sides.contains(Side.BOTTOM)) {
                    if (isRealBottomCollision(entity, other, sides)) {
                        if (other instanceof BouncyTile) {
                            velY = -Math.abs(velY);
                            accY = Math.min(accY, 0);
                        } else {
                            velY = Math.min(velY, 0);
                            accY = Math.min(accY, 0);
                        }
                        corrY = Math.max(0f, Math.min(corrY, other.getRect(0f).getY() - entity.getRect(0f).getMaxY() - EPS));
                    } else {
                        corrX = getFakeBottomCollisionCorrection(entity, other, sides);
                    }
                }
                if (sides.contains(Side.RIGHT)) {
                    if (other instanceof BouncyTile) {
                        velX = -Math.abs(velX);
                        accX = Math.min(accX, 0);
                    } else {
                        velX = Math.min(velX, 0);
                        accX = Math.min(accX, 0);
                    }
                }
            } else {
                velX = 0;
                velY = 0;
                accX = 0;
                accY = 0;
            }
            for (Side s : sides) {
                possiblePos = possiblePos.setSide(s, otherRect.getSide(s.getOpposite()));
                if (possiblePos == null) {
                    if (entity instanceof Player) {
                        throw new RuntimeException("crushed!");
                    }
                    break;
                }
            }
        }
        Rect r = entity.getRect(0f);
        if (corrX != 0.0f) {
            r = r.translate(corrX, 0f);
        }
        if (!Float.isInfinite(corrY)) {
            r = r.translate(0f, corrY);
        }
        r = r.translateInto(possiblePos);
        if (r == null) {
            throw new RuntimeException("crushed again!");
        }
        return new PhysicalState(r, velX, velY, accX, accY);
    }

    @Override
    public PhysicalState rehandleCollision(PhysicallyStateful entity, Collection<RectCollision> collisions) {
        System.err.println("Warning: rehandling collisions for: " + entity);
        return entity.getPhysicalState().snapshot(backstep);
    }
}
