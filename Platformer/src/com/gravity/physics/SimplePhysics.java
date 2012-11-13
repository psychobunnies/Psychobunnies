package com.gravity.physics;

import com.gravity.entity.Entity;

public class SimplePhysics implements Physics {

    SimplePhysics() {
        // No-op
    }

    @Override
    public PhysicalState computePhysics(Entity entity) {
        return entity.getPhysicalState();
    }

}
