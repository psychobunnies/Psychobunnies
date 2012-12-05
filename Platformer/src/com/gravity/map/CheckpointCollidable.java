package com.gravity.map;

import java.util.Collection;

import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.physics.Collidable;
import com.gravity.physics.RectCollision;

/**
 * Class to represent checkpoints
 * 
 * @author phulin
 * 
 */
public class CheckpointCollidable extends StaticCollidable {

    private Checkpoint checkpoint;

    public CheckpointCollidable(Checkpoint checkpoint, Rect shape) {
        super(shape);

        this.checkpoint = checkpoint;
    }

    @Override
    public void handleCollisions(float millis, Collection<RectCollision> collection) {
        for (RectCollision coll : collection) {
            Collidable other = coll.getOtherEntity(this);
            if (other instanceof Player) {
                checkpoint.playerPassed((Player) other);
            }
        }
    }

    @Override
    public boolean causesCollisionsWith(Collidable other) {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CheckpointCollidable [super=");
        builder.append(super.toString());
        builder.append(", checkpoint=");
        builder.append(checkpoint);
        builder.append("]");
        return builder.toString();
    }

}
