package com.gravity.map.tiles;

import java.util.Collection;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.physics.Collidable;
import com.gravity.physics.RectCollision;
import com.gravity.root.GameplayControl;
import com.gravity.root.Renderer;
import com.gravity.root.UpdateCycling;

/**
 * Represents a moving platform, e.g.
 * 
 * @author phulin
 */

public class MovingCollidable implements Collidable, UpdateCycling, Renderer {

    private GameplayControl controller;
    private TileRendererDelegate renderer;
    private Rect shape;
    private Vector2f origPosition;
    private Vector2f velForward, velBackward, finalPosition;
    private boolean reversed;

    public MovingCollidable(GameplayControl controller, TileRendererDelegate renderer, Rect shape, int transX, int transY, float speed) {
        this(controller, renderer, shape, transX, transY, speed, speed);
    }

    public MovingCollidable(GameplayControl controller, TileRendererDelegate renderer, Rect shape, int transX, int transY, float speedForward, float speedBackward) {
        this.controller = controller;
        this.renderer = renderer;
        this.shape = shape;
        this.origPosition = shape.getPoint(Rect.Corner.TOPLEFT);

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
    public Vector2f getPosition(float millis) {
        return getRect(millis).getPoint(Rect.Corner.TOPLEFT);
    }

    @Override
    public Rect getRect(float millis) {
        return getRectWithReversal(millis).rect;
    }

    private class RectWithReversal {
        public final boolean reverse;
        public final Rect rect;

        public RectWithReversal(boolean reverse, Rect rect) {
            this.reverse = reverse;
            this.rect = rect;
        }
    }

    public RectWithReversal getRectWithReversal(float millis) {
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

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        renderer.render(g, offsetX, offsetY, getRect(0));
    }
}
