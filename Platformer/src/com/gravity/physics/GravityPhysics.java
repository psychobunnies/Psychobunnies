package com.gravity.physics;

import java.util.Collection;
import java.util.EnumSet;

import com.google.common.base.Preconditions;
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

    GravityPhysics(CollisionEngine collisionEngine, float gravity, float backstep, float offsetGroundCheck) {
        Preconditions.checkArgument(backstep <= 0f, "Backstep has to be non-positive.");
        this.collisionEngine = collisionEngine;
        this.gravity = gravity;
        this.backstep = backstep;
        this.offsetGroundCheck = offsetGroundCheck;
    }

    public boolean isOnGround(PhysicalState state) {
        Rect collider = state.getRectangle().translate(0, offsetGroundCheck);
        //@formatter:off
        return collisionEngine.collidesAgainstLayer(0, 
                collider, 
                LayeredCollisionEngine.FLORA_LAYER);
        //@formatter:on
    }

    @Override
    public PhysicalState computePhysics(PhysicallyStateful entity) {
        if (isOnGround(entity.getPhysicalState())) {
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
        float accY = state.accY;
        for (RectCollision c : collisions) {
            EnumSet<Side> sides = c.getMyCollisions(entity);
            Preconditions.checkArgument(sides != null, "Collision passed did not involve entity: " + entity + ", " + c);

            if (Side.isSimpleSet(sides)) {
                if (sides.contains(Side.TOP)) {
                    velY = Math.max(velY, 0);
                }
                if (sides.contains(Side.LEFT)) {
                    velX = Math.max(velX, 0);
                }
                if (sides.contains(Side.BOTTOM)) {
                    velY = Math.min(velY, 0);
                    accY = 0;
                }
                if (sides.contains(Side.RIGHT)) {
                    velX = Math.min(velX, 0);
                }
            } else {
                velX = 0;
                velY = 0;
            }
        }
        return new PhysicalState(entity.getRect(0), velX, velY, 0, accY);
    }

    @Override
    public PhysicalState rehandleCollision(PhysicallyStateful entity, Collection<RectCollision> collisions) {
        System.err.println("Warning: rehandling collisions for: " + entity);
        return entity.getPhysicalState().snapshot(backstep);
    }
}
