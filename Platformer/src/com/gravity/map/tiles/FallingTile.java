package com.gravity.map.tiles;

import java.util.Collection;
import java.util.Random;

import org.newdawn.slick.Graphics;

import com.gravity.entity.AbstractEntity;
import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.levels.GameplayState;
import com.gravity.levels.Renderer;
import com.gravity.physics.PhysicalState;
import com.gravity.physics.PhysicsFactory;
import com.gravity.physics.RectCollision;

public class FallingTile extends AbstractEntity implements Renderer {
    public final int MILLIS_TO_FALL = 20000;
    public final float FALL_ACC = PhysicsFactory.DEFAULT_GRAVITY * 2;
    public final float MAX_VEL = 0.15f;

    private TileRenderer renderer;
    private GameplayState gameState;

    private float startX, startY;
    private boolean falling = false;
    private Random rand = new Random();
    private int hitReduction;

    public FallingTile(GameplayState gameState, Rect shape, int hitReduction, TileRenderer renderer) {
        super(new PhysicalState(shape, 0, 0, 0, 0, 0));
        this.renderer = renderer;
        this.gameState = gameState;
        this.startX = this.state.getPosition().x;
        this.startY = this.state.getPosition().y;
        this.hitReduction = hitReduction;
    }

    @Override
    public void finishUpdate(float millis) {
        if (falling) {
            state = state.snapshot(millis);
        }
    }

    @Override
    public void startUpdate(float millis) {
        // Set it to fall randomly at some point
        if (rand.nextInt(MILLIS_TO_FALL) < millis) {
            falling = true;
            state = state.setAcceleration(0, FALL_ACC);
        }
        if (state.velY >= MAX_VEL) {
            state = state.setVelocity(0, MAX_VEL);
            state = state.setAcceleration(0, 0);
        }
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        renderer.render(g, offsetX - hitReduction, offsetY - hitReduction, state.getRectangle());
        // if we ever need hitboxes again
        // g.setColor(Color.cyan);
        // g.draw(state.getRectangle().translate(offsetX, offsetY).toShape());
    }

    @Override
    public void handleCollisions(float millis, Collection<RectCollision> collection) {
        for (RectCollision c : collection) {
            if (c.getOtherEntity(this) instanceof Player) {
                gameState.playerDies((Player) c.getOtherEntity(this));
            }
        }
        state = state.killMovement();
        state = state.teleport(startX, startY);
    }

    @Override
    public void rehandleCollisions(float millis, Collection<RectCollision> collisions) {
        // no-op
    }

    @Override
    public String toString() {
        return "FallingTile [startX=" + startX + ", startY=" + startY + ", falling=" + falling + ", state=" + state + "]";
    }

    @Override
    public void unavoidableCollisionFound() {
        // no-op
    }

}
