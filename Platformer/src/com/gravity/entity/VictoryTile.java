package com.gravity.entity;

import java.util.Collection;

import com.google.common.base.Preconditions;
import com.gravity.geom.Rect;
import com.gravity.physics.RectCollision;
import com.gravity.victory.VictoryController;

public final class VictoryTile extends TileWorldCollidable {

    private VictoryController controller = null;

    public VictoryTile(Rect shape) {
        super(shape);
    }

    public void initialize(VictoryController controller) {
        Preconditions.checkNotNull(controller, "Controller may not be null.");
        this.controller = controller;
    }

    @Override
    public boolean isPassThrough() {
        return true;
    }

    @Override
    public void handleCollisions(float millis, Collection<RectCollision> collisions) {
        Preconditions.checkNotNull(controller, "The tile was not initialized properly. Please set a VictoryController.");
        for (RectCollision coll : collisions) {
            controller.collidableOnVictoryTile(coll.getOtherEntity(this));
        }
    }
}
