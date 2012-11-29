package com.gravity.physics;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.gravity.entity.Entity;
import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.geom.Rect.Side;
import com.gravity.map.tiles.BouncyTile;
import com.gravity.map.tiles.MovingEntity;

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
    private final float groundFriction;
    private final float frictionStopCutoff; // the velocity below which friction makes you stop completely
    private final float frictionAccelRatio; // the maximum fraction of your velocity that friction acceleration may represent
    private final float movingTilePositionFeather;
    private final float maxOnGroundFallSpeed;
    private static final float EPS = 1e-4f;

    GravityPhysics(CollisionEngine collisionEngine, float gravity, float backstep, float offsetGroundCheck, float groundFriction,
            float frictionStopCutoff, float frictionAccelRatio, float movingTilePositionFeather, float maxOnGroundFallSpeed) {
        Preconditions.checkArgument(backstep <= 0f, "Backstep has to be non-positive.");
        Preconditions.checkArgument(movingTilePositionFeather > 0, "Moving tile position feather amount must be positive.");
        this.collisionEngine = collisionEngine;
        this.gravity = gravity;
        this.backstep = backstep;
        this.offsetGroundCheck = offsetGroundCheck;
        this.groundFriction = groundFriction;
        this.frictionStopCutoff = frictionStopCutoff;
        this.frictionAccelRatio = frictionAccelRatio;
        this.movingTilePositionFeather = movingTilePositionFeather;
        this.maxOnGroundFallSpeed = maxOnGroundFallSpeed;
    }

    public List<Collidable> entitiesHitOnGround(Entity entity) {
        Rect collider = entity.getPhysicalState().getRectangle().translate(0, offsetGroundCheck);
        List<Collidable> collisions = collisionEngine.collisionsInLayer(0f, collider, LayeredCollisionEngine.FLORA_LAYER, false);

        for (Collidable c : collisions) {
            c.handleCollisions(0f, Lists.newArrayList(new RectCollision(entity, c, 0f, null, null)));
        }

        List<Collidable> soln = Lists.newArrayList();
        for (Collidable c : collisions) {
            if (c.causesCollisionsWith(entity)) {
                soln.add(c);
            }
        }
        return soln;
    }

    @Override
    public PhysicalState computePhysics(Entity entity) {
        List<Collidable> coll = entitiesHitOnGround(entity);
        if (!coll.isEmpty()) {
            PhysicalState state = entity.getPhysicalState();
            if (state.velY >= 0) {
                float minPositiveYVel = 0f;
                float minY = Float.POSITIVE_INFINITY;
                boolean isBouncy = false;
                for (Collidable c : coll) {
                    if (c instanceof BouncyTile) {
                        isBouncy = true;
                        break;
                    } else {
                        float tmp = c.getPhysicalState().getRectangle().getY();
                        if (minY >= tmp) {
                            minY = tmp;
                            minPositiveYVel = Math.min(minPositiveYVel, c.getPhysicalState().velY);
                        }
                    }
                }
                if (!isBouncy) {
                    Rect r = state.getRectangle().translate(0, minY - state.getRectangle().getMaxY() - EPS);
                    if (minPositiveYVel > maxOnGroundFallSpeed) {
                        r = state.getRectangle();
                    }
                    state = new PhysicalState(r, state.velX, Math.min(state.velY, 0f), state.accX, Math.min(state.accY, 0));
                }
            }
            if (Math.abs(state.velX) <= frictionStopCutoff) {
                state = state.setVelocity(0f, state.velY);
            } else {
                state = state.addAcceleration(calculateFrictionalAcceleration(state.velX), 0);
            }
            return state;
        } else {
            return entity.getPhysicalState().addAcceleration(0f, gravity);
        }
    }

    private float calculateFrictionalAcceleration(float velocity) {
        return -Math.signum(velocity) * Math.min(groundFriction, Math.abs(velocity) / frictionAccelRatio);
    }

    @Override
    public PhysicalState handleCollision(Entity entity, float millis, Collection<RectCollision> collisions) {
        PhysicalState state = entity.getPhysicalState();
        boolean movedToGround = false;
        float velX = state.velX;
        float velY = state.velY;
        float accX = state.accX;
        float accY = state.accY;

        Rect r = entity.getPhysicalState().getRectangle();
        for (RectCollision c : collisions) {
            Collidable other = c.getOtherEntity(entity);
            EnumSet<Side> sides = c.getMyCollisions(entity);
            Preconditions.checkArgument(sides != null, "Collision passed did not involve entity: " + entity + ", " + c);

            if (Side.isSimpleSet(sides)) {
                if (sides.contains(Side.TOP)) {
                    if (other instanceof BouncyTile) {
                        velY = Math.abs(velY);
                        accY = Math.max(accY, 0);
                    } else if (other instanceof MovingEntity && other.getPhysicalState().getVelocity().y > 0) {
                        r = r.translate(0f, Math.max(movingTilePositionFeather, other.getPhysicalState().getVelocity().y * millis));
                        if (collisionEngine.collisionsInLayer(millis, r, LayeredCollisionEngine.FLORA_LAYER, true).isEmpty()) {
                            velY = Math.max(velY, 0);
                            accY = Math.max(accY, 0);
                        } else {
                            entity.unavoidableCollisionFound();
                        }
                    } else {
                        velY = Math.max(velY, 0);
                        accY = Math.max(accY, 0);
                    }
                }
                if (sides.contains(Side.LEFT)) {
                    if (other instanceof BouncyTile) {
                        velX = Math.abs(velX);
                        accX = Math.max(accX, 0);
                    } else if (other instanceof MovingEntity && other.getPhysicalState().getVelocity().x > 0) {
                        r = r.translate(Math.max(movingTilePositionFeather, other.getPhysicalState().getVelocity().x * millis), 0f);
                        if (collisionEngine.collisionsInLayer(millis, r, LayeredCollisionEngine.FLORA_LAYER, true).isEmpty()) {
                            velX = Math.max(velX, 0);

                            // HACK - due to delay in processing friction
                            if (velX == 0) {
                                accX = 0;
                            } else {
                                accX = Math.max(accX, 0);
                            }
                        } else {
                            entity.unavoidableCollisionFound();
                        }
                    } else {
                        velX = Math.max(velX, 0);

                        // HACK - due to delay in processing friction
                        if (velX == 0) {
                            accX = 0;
                        } else {
                            accX = Math.max(accX, 0);
                        }
                    }
                }
                if (sides.contains(Side.BOTTOM)) {
                    if (other instanceof BouncyTile) {
                        velY = -Math.abs(velY);
                        accY = Math.min(accY, 0);
                    } else if (other instanceof MovingEntity && other.getPhysicalState().getVelocity().y < 0) {
                        r = r.translate(0f, Math.min(-movingTilePositionFeather, other.getPhysicalState().getVelocity().y * millis));
                        if (collisionEngine.collisionsInLayer(millis, r, LayeredCollisionEngine.FLORA_LAYER, true).isEmpty()) {
                            velY = Math.min(velY, 0);
                            accY = Math.min(accY, 0);
                        } else {
                            entity.unavoidableCollisionFound();
                        }
                    } else {
                        velY = Math.min(velY, 0);
                        accY = Math.min(accY, 0);
                        if (!movedToGround) {
                            movedToGround = true;
                            Rect nr = r.translate(0f, other.getPhysicalState().getRectangle().getY() - r.getMaxY() - EPS);
                            if (collisionEngine.collisionsInLayer(millis, nr, LayeredCollisionEngine.FLORA_LAYER, true).isEmpty()) {
                                // move the collidable exactly on the ground
                                r = nr;
                            }
                        }
                    }
                }
                if (sides.contains(Side.RIGHT)) {
                    if (other instanceof BouncyTile) {
                        velX = -Math.abs(velX);
                        accX = Math.min(accX, 0);
                    } else if (other instanceof MovingEntity && other.getPhysicalState().getVelocity().x < 0) {
                        r = r.translate(Math.min(-movingTilePositionFeather, other.getPhysicalState().getVelocity().x * millis), 0f);
                        if (collisionEngine.collisionsInLayer(millis, r, LayeredCollisionEngine.FLORA_LAYER, true).isEmpty()) {
                            velX = Math.min(velX, 0);

                            // HACK - due to delay in processing friction
                            if (velX == 0) {
                                accX = 0;
                            } else {
                                accX = Math.min(accX, 0);
                            }
                        } else {
                            entity.unavoidableCollisionFound();
                        }
                    } else {
                        velX = Math.min(velX, 0);

                        // HACK - due to delay in processing friction
                        if (velX == 0) {
                            accX = 0;
                        } else {
                            accX = Math.min(accX, 0);
                        }
                    }
                }
            } else {
                velX = 0;
                velY = 0;
                accX = 0;
                accY = 0;
            }
        }
        return new PhysicalState(r, velX, velY, accX, accY);
    }

    @Override
    public PhysicalState rehandleCollision(Entity entity, float millis, Collection<RectCollision> collisions) {
        System.err.println("Warning: rehandling collisions for: entity=" + entity + "; collisions=" + collisions.toString());
        Rect possiblePos = new Rect(-1000000.0f, -1000000.0f, 2000000.0f, 2000000.0f);
        for (RectCollision c : collisions) {
            Collidable other = c.getOtherEntity(entity);
            Rect otherRect = other.getPhysicalStateAt(millis).getRectangle();
            EnumSet<Side> sides = c.getMyCollisions(entity);
            Preconditions.checkArgument(sides != null, "Collision passed did not involve entity: " + entity + ", " + c);

            for (Side s : sides) {
                possiblePos = possiblePos.setSide(s, otherRect.getSide(s.getOpposite()));
                if (possiblePos == null) {
                    if (entity instanceof Player) {
                        System.err.println("Crushing player; no possible positions");
                    }
                    entity.unavoidableCollisionFound();
                    break;
                }
            }
        }
        Rect r = entity.getPhysicalState().getRectangle();

        r = r.translateIntoWithMargin(possiblePos, 0.2f);
        if (r == null) {
            if (entity instanceof Player) {
                System.err.println("Crushing player; possible positions too small");
            }
            entity.unavoidableCollisionFound();
            return entity.getPhysicalState().snapshot(backstep);
        }
        return entity.getPhysicalState().teleport(r.getX(), r.getY()).killMovement();
    }
}
