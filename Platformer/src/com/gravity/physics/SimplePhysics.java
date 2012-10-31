package com.gravity.physics;

import java.util.Collection;

import com.gravity.entity.Entity;
import com.gravity.entity.PhysicallyStateful;

public class SimplePhysics implements Physics {

    SimplePhysics() {
        // No-op
    }

    @Override
    public PhysicalState computePhysics(PhysicallyStateful entity) {
        return entity.getPhysicalState();
    }

    @Override
    public PhysicalState handleCollision(Entity entity, Collection<RectCollision> collisions) {
        return entity.getPhysicalState().killMovement();
    }

    @Override
    public PhysicalState rehandleCollision(PhysicallyStateful entity, Collection<RectCollision> collisions) {
        PhysicalState state = entity.getPhysicalState();
        System.err.println("WARNING: Rehandling collision for entity " + entity + " at " + state + " stupidly");
        return state.killMovement();
    }

}
