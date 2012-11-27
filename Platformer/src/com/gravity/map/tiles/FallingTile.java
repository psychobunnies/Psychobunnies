package com.gravity.map.tiles;

import java.util.Collection;
import java.util.Random;

import org.newdawn.slick.Color;
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

    private TileRendererDelegate renderer;
    private GameplayState gameState;

    private float startX, startY;
    private boolean falling = false;
    private Random rand = new Random();

    public FallingTile(GameplayState gameState, Rect shape, TileRendererDelegate renderer, int x, int y) {
        super(new PhysicalState(shape.translateTo(x * 32, y * 32), 0, 0, 0, 0));
        this.renderer = renderer;
        this.gameState = gameState;
        this.startX = this.state.getPosition().x;
        this.startY = this.state.getPosition().y;
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
        renderer.render(g, offsetX, offsetY, state.getRectangle());
        g.setColor(Color.cyan);
        g.draw(state.getRectangle().translate(offsetX, offsetY).toShape());
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

}
