package com.gravity.map.tiles;

import com.google.common.base.Preconditions;
import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.map.StaticCollidable;
import com.gravity.physics.Collidable;
import com.gravity.physics.CollisionEngine;
import com.gravity.physics.LayeredCollisionEngine;

public final class DisappearingTile extends StaticCollidable {

    private final DisappearingTileController controller;
    private final CollisionEngine engine;

    public DisappearingTile(Rect shape, DisappearingTileController controller, CollisionEngine engine) {
        super(shape);
        Preconditions.checkArgument(controller != null, "Controller may not be null.");
        Preconditions.checkArgument(engine != null, "Collision engine may not be null.");
        this.controller = controller;
        this.engine = engine;
    }

    @Override
    public boolean causesCollisionsWith(Collidable other) {
        return other instanceof Player && controller.collisionsEnabled();
    }

    /** Method that the controller uses to check if it's safe for all tiles to materialize */
    public boolean isColliding() {
        return !engine.collisionsInLayer(0f, this.getRect(0f), LayeredCollisionEngine.FAUNA_LAYER).isEmpty();
    }

}
