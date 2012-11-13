package com.gravity.physics;

import java.util.Collection;

import com.google.common.base.Preconditions;
import com.gravity.entity.Entity;

public class RewindCollisionStrategy implements CollisionStrategy {

    private final float backstep;

    public RewindCollisionStrategy(float backstep) {
        Preconditions.checkArgument(backstep <= 0f, "Backstep has to be non-positive.");
        this.backstep = backstep;
    }

    @Override
    public PhysicalState handleCollision(Entity entity, Collection<RectCollision> collisions) {
        return entity.getPhysicalState().snapshot(backstep);
    }

}
