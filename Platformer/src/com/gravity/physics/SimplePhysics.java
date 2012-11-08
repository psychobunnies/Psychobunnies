package com.gravity.physics;

import java.util.Collection;

import com.gravity.entity.Entity;
import com.gravity.entity.PhysicallyStateful;

public class SimplePhysics implements Physics {

    SimplePhysics() {
        // No-op
    }

    @Override
    public PhysicalState computePhysics(Entity entity) {
        return entity.getPhysicalState();
    }

    @Override
    public PhysicalState handleCollision(Entity entity, Collection<RectCollision> collisions) {
        for (RectCollision c : collisions) {
            if (!c.getOtherEntity(entity).isPassThrough()) {
                return entity.getPhysicalState().killMovement();
            }
        }
        return entity.getPhysicalState();
    }

    @Override
    public PhysicalState rehandleCollision(PhysicallyStateful entity, Collection<RectCollision> collisions) {
        PhysicalState state = entity.getPhysicalState();
        System.err.println("WARNING: Rehandling collision for entity " + entity + " at " + state + " stupidly");
        return state.killMovement();
    }

}
