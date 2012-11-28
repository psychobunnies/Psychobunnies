package com.gravity.physics;

import java.util.Collection;

import com.gravity.entity.Entity;

public class SimplePhysics implements Physics {

    SimplePhysics() {
        // No-op
    }

    @Override
    public PhysicalState computePhysics(Entity entity) {
        return entity.getPhysicalState();
    }

    @Override
    public PhysicalState handleCollision(Entity entity, float millis, Collection<RectCollision> collisions) {
        return entity.getPhysicalState().killMovement();
    }

    @Override
    public PhysicalState rehandleCollision(Entity entity, float millis, Collection<RectCollision> collisions) {
        PhysicalState state = entity.getPhysicalState();
        System.err.println("WARNING: Rehandling collision for entity " + entity + " at " + state + " stupidly");
        return state.killMovement();
    }

}
