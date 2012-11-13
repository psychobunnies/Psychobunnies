package com.gravity.physics;

import java.util.List;

import com.google.common.collect.Lists;
import com.gravity.entity.Entity;
import com.gravity.geom.Rect;

/**
 * A Physics simulator which assumes gravity, but no bouncing.
 * 
 * @author xiao, predrag
 * 
 */
public class GravityPhysics implements Physics {

    private final CollisionEngine collisionEngine;
    private final float gravity;
    private final float offsetGroundCheck;

    GravityPhysics(CollisionEngine collisionEngine, float gravity, float offsetGroundCheck) {
        this.collisionEngine = collisionEngine;
        this.gravity = gravity;
        this.offsetGroundCheck = offsetGroundCheck;
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

    @Override
    public PhysicalState computePhysics(Entity entity) {
        if (isOnGround(entity)) {
            return entity.getPhysicalState();
        } else {
            return entity.getPhysicalState().addAcceleration(0f, gravity);
        }
    }
}
