package com.gravity.map.tiles;

import java.util.Collection;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.levels.GameplayControl;
import com.gravity.levels.UpdateCycling;
import com.gravity.physics.Collidable;
import com.gravity.physics.PhysicalState;
import com.gravity.physics.RectCollision;

/**
 * Represents a moving platform, e.g.
 * 
 * @author phulin
 */

// TODO implement entity instead of Collidable, until that's done this class WILL NOT WORK AT ALL
public class MovingTile implements Collidable, UpdateCycling {

    private GameplayControl controller;
    private Rect shape;
    private Vector2f origPosition;
    private Vector2f velForward, velBackward, finalPosition;
    private boolean reversed;
    private PhysicalState state;

    public MovingTile(GameplayControl controller, Rect shape, int transX, int transY, float speed) {
        this(controller, shape, transX, transY, speed, speed);
    }

    public MovingTile(GameplayControl controller, Rect shape, int transX, int transY, float speedForward, float speedBackward) {
        this.controller = controller;
        this.shape = shape;
        this.origPosition = shape.getPoint(Rect.Corner.TOPLEFT);

        // HACK - see TODO at top of class
        this.state = new PhysicalState(shape, 0f, 0f);

        Vector2f trans = new Vector2f(transX, transY);
        velForward = trans.getNormal();
        velForward.scale(speedForward / 1000.0f);
        velBackward = trans.getNormal();
        velBackward.scale(-speedBackward / 1000.0f);
        finalPosition = origPosition.copy().add(trans);

        reversed = false;
    }

    public Vector2f getOrigPosition() {
        return origPosition;
    }

    @Override
    public PhysicalState getPhysicalState() {
        return state;
    }

    @Override
    public PhysicalState getPhysicalStateAt(float millis) {
        return state;
    }

    @Override
    public void setPhysicalState(PhysicalState newState) {
        state = newState;
    }

    private class RectWithReversal {
        public final boolean reverse;
        public final Rect rect;

        public RectWithReversal(boolean reverse, Rect rect) {
            this.reverse = reverse;
            this.rect = rect;
        }
    }

    private RectWithReversal getRectWithReversal(float millis) {
        boolean reverse;
        Vector2f position;
        if (reversed) {
            Vector2f potentialResult = shape.translate(velBackward.copy().scale(millis)).getPoint(Rect.Corner.TOPLEFT);
            reverse = potentialResult.distance(finalPosition) > origPosition.distance(finalPosition);
            if (reverse) {
                position = origPosition.copy().scale(2.0f).sub(potentialResult);
            } else {
                position = potentialResult;
            }
        } else {
            Vector2f potentialResult = shape.translate(velForward.copy().scale(millis)).getPoint(Rect.Corner.TOPLEFT);
            reverse = potentialResult.distance(origPosition) > finalPosition.distance(origPosition);
            if (reverse) {
                position = finalPosition.copy().scale(2.0f).sub(potentialResult);
            } else {
                position = potentialResult;
            }
        }
        return new RectWithReversal(reverse ^ reversed, shape.translateTo(position.x, position.y));
    }

    @Override
    public void handleCollisions(float millis, Collection<RectCollision> collection) {
        // no-op
    }

    @Override
    public void rehandleCollisions(float millis, Collection<RectCollision> collisions) {
        // This should mean we're trying to crush a player.
        for (RectCollision coll : collisions) {
            Collidable c = coll.getOtherEntity(this);
            if (c instanceof Player) {
                controller.playerDies((Player) c);
            }
        }
    }

    @Override
    public boolean causesCollisionsWith(Collidable other) {
        return true;
    }

    @Override
    public void finishUpdate(float millis) {
        RectWithReversal result = getRectWithReversal(millis);
        reversed = result.reverse;
        shape = result.rect;
    }

    @Override
    public void startUpdate(float millis) {
        // no-op
    }

}
