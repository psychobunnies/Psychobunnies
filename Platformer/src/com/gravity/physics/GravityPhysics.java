package com.gravity.physics;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.gravity.entity.Entity;
import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.geom.Rect.RectException;
import com.gravity.geom.Rect.Side;
import com.gravity.map.tiles.BouncyTile;
import com.gravity.map.tiles.MovingEntity;
import com.gravity.root.GameSounds;
import com.gravity.root.GameSounds.Event;

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
    private final float offsetSideCheck;
    private final float groundFriction;
    private final float frictionStopCutoff; // the velocity below which friction makes you stop completely
    private final float frictionAccelRatio; // the maximum fraction of your velocity that friction acceleration may represent
    private final float movingTilePositionFeather;
    private final float maxOnGroundFallSpeed;
    private final float maxFallingSpeed;
    private final float fallingSpeedRevertMultiplier;
    private static final float EPS = 1e-4f;

    GravityPhysics(CollisionEngine collisionEngine, float gravity, float backstep, float offsetSideCheck, float groundFriction,
            float frictionStopCutoff, float frictionAccelRatio, float movingTilePositionFeather, float maxOnGroundFallSpeed, float maxFallingSpeed,
            float fallingSpeedRevertMultiplier) {
        Preconditions.checkArgument(backstep <= 0f, "Backstep has to be non-positive.");
        Preconditions.checkArgument(movingTilePositionFeather > 0, "Moving tile position feather amount must be positive.");
        this.collisionEngine = collisionEngine;
        this.gravity = gravity;
        this.backstep = backstep;
        this.offsetSideCheck = offsetSideCheck;
        this.groundFriction = groundFriction;
        this.frictionStopCutoff = frictionStopCutoff;
        this.frictionAccelRatio = frictionAccelRatio;
        this.movingTilePositionFeather = movingTilePositionFeather;
        this.maxOnGroundFallSpeed = maxOnGroundFallSpeed;
        this.maxFallingSpeed = maxFallingSpeed;
        this.fallingSpeedRevertMultiplier = fallingSpeedRevertMultiplier;
    }

    public List<Collidable> entitiesHitLeft(Entity entity) {
        Rect collider = entity.getPhysicalState().getRectangle().translate(-offsetSideCheck, 0);
        return entitiesHitOnSide(entity, collider);
    }

    public List<Collidable> entitiesHitRight(Entity entity) {
        Rect collider = entity.getPhysicalState().getRectangle().translate(offsetSideCheck, 0);
        return entitiesHitOnSide(entity, collider);
    }

    public List<Collidable> entitiesHitOnGround(Entity entity) {
        Rect collider = entity.getPhysicalState().getRectangle().translate(0, offsetSideCheck);
        return entitiesHitOnSide(entity, collider);
    }

    private List<Collidable> entitiesHitOnSide(Entity entity, Rect rectToUse) {
        List<Collidable> collisions = collisionEngine.collisionsInLayer(0f, rectToUse, LayeredCollisionEngine.FLORA_LAYER, false);

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
        List<Collidable> coll;
        PhysicalState state = entity.getPhysicalState();

        boolean hasBouncy = false;
        if (state.velX > 0) {
            coll = entitiesHitRight(entity);
            for (Collidable c : coll) {
                if (c instanceof BouncyTile) {
                    hasBouncy = true;
                    break;
                }
            }
            if (!coll.isEmpty() && !hasBouncy) {
                state = state.setVelocity(0, state.velY);
            }
        } else if (state.velX < 0) {
            coll = entitiesHitLeft(entity);
            for (Collidable c : coll) {
                if (c instanceof BouncyTile) {
                    hasBouncy = true;
                    break;
                }
            }
            if (!coll.isEmpty() && !hasBouncy) {
                state = state.setVelocity(0, state.velY);
            }
        }

        if (state.velY > maxFallingSpeed) {
            System.err.println("WARNING: Reverting max vertical speed for by entity " + entity.toString());
            Preconditions.checkArgument(state.velY > maxFallingSpeed * fallingSpeedRevertMultiplier);
            state = state.setVelocity(state.velX, maxFallingSpeed * fallingSpeedRevertMultiplier);
        } else if (state.velY < -maxFallingSpeed) {
            System.err.println("WARNING: Reverting max vertical speed for by entity " + entity.toString());
            Preconditions.checkArgument(state.velY < -maxFallingSpeed * fallingSpeedRevertMultiplier);
            state = state.setVelocity(state.velX, -maxFallingSpeed * fallingSpeedRevertMultiplier);
        }

        coll = entitiesHitOnGround(entity);
        boolean movingTileMoved = false;
        if (!coll.isEmpty()) {
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
                        if (!movingTileMoved && c instanceof MovingEntity) {
                            MovingEntity mov = (MovingEntity) c;
                            movingTileMoved = true;
                            float surfaceVelX = mov.getPhysicalState().velX;
                            state = new PhysicalState(state.getRectangle(), state.velX, state.velY, state.accX, state.accY, surfaceVelX);
                        }
                    }
                }
                if (!isBouncy) {
                    Rect r = state.getRectangle().translate(0, minY - state.getRectangle().getMaxY() - EPS);
                    if (minPositiveYVel > maxOnGroundFallSpeed) {
                        r = state.getRectangle();
                    }
                    state = new PhysicalState(r, state.velX, Math.min(state.velY, 0f), state.accX, Math.min(state.accY, 0), state.surfaceVelX);
                }
                if (!movingTileMoved) {
                    state = state.removeSurfaceSpeed();
                }
            }
            if (Math.abs(state.velX) <= frictionStopCutoff) {
                state = state.setVelocity(0f, state.velY);
            } else {
                state = state.addAcceleration(calculateFrictionalAcceleration(state.velX), 0);
            }
            return state;
        } else {
            return state.addAcceleration(0f, gravity);
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
        float scaleBounce = 1f;
        float surfX = state.surfaceVelX;

        Rect r = entity.getPhysicalState().getRectangle();
        for (RectCollision c : collisions) {
            Collidable other = c.getOtherEntity(entity);
            EnumSet<Side> sides = c.getMyCollisions(entity);
            Preconditions.checkArgument(sides != null, "Collision passed did not involve entity: " + entity + ", " + c);

            if (Side.isSimpleSet(sides)) {
                if (sides.contains(Side.TOP)) {
                    GameSounds.playSoundFor(Event.BONK);
                    if (other instanceof BouncyTile) {
                        velY = scaleBounce * Math.abs(velY);
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
                        velX = scaleBounce * Math.abs(velX);
                        accX = Math.max(accX, 0);
                    } else if (other instanceof MovingEntity && other.getPhysicalState().getVelocity().x > 0) {
                        r = r.translate(Math.max(movingTilePositionFeather, other.getPhysicalState().getVelocity().x * millis), 0f);
                        if (collisionEngine.collisionsInLayer(millis, r, LayeredCollisionEngine.FLORA_LAYER, true).isEmpty()) {
                            velX = Math.max(velX, 0);
                            surfX = Math.max(surfX, 0);

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
                        surfX = Math.max(surfX, 0);

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
                        velY = -scaleBounce * Math.abs(velY);
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
                            Rect nr = r.translate(0f, other.getPhysicalState().getRectangle().getY() - r.getMaxY() - EPS);
                            if (collisionEngine.collisionsInLayer(millis, nr, LayeredCollisionEngine.FLORA_LAYER, true).isEmpty()) {
                                // move the collidable exactly on the ground
                                r = nr;
                                movedToGround = true;
                            }
                        }
                    }
                }
                if (sides.contains(Side.RIGHT)) {
                    if (other instanceof BouncyTile) {
                        velX = -scaleBounce * Math.abs(velX);
                        accX = Math.min(accX, 0);
                    } else if (other instanceof MovingEntity && other.getPhysicalState().getVelocity().x < 0) {
                        r = r.translate(Math.min(-movingTilePositionFeather, other.getPhysicalState().getVelocity().x * millis), 0f);
                        if (collisionEngine.collisionsInLayer(millis, r, LayeredCollisionEngine.FLORA_LAYER, true).isEmpty()) {
                            velX = Math.min(velX, 0);
                            surfX = Math.min(surfX, 0);

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
                        surfX = Math.min(surfX, 0);

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
                surfX = 0;
            }
        }
        return new PhysicalState(r, velX, velY, accX, accY, surfX);
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
                try {
                    possiblePos = possiblePos.setSide(s, otherRect.getSide(s.getOpposite()));
                } catch (RectException e) {
                    if (entity instanceof Player) {
                        System.err.println("Crushing player; no possible positions");
                        e.printStackTrace();
                    }
                    entity.unavoidableCollisionFound();
                    return entity.getPhysicalState().snapshot(backstep);
                }
            }
        }
        Rect r = entity.getPhysicalState().getRectangle();

        try {
            r = r.translateIntoWithMargin(possiblePos, 0.2f);
        } catch (RectException e) {
            if (entity instanceof Player) {
                System.err.println("Crushing player; possible positions too small");
                e.printStackTrace();
            }
            entity.unavoidableCollisionFound();
            return entity.getPhysicalState().snapshot(backstep);
        }
        return entity.getPhysicalState().teleport(r.getX(), r.getY()).killMovement();
    }
}
