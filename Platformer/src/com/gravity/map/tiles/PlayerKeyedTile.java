package com.gravity.map.tiles;

import java.util.Collection;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.Layer;

import com.gravity.entity.AbstractEntity;
import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.levels.Renderer;
import com.gravity.levels.Resetable;
import com.gravity.map.TileType;
import com.gravity.physics.Collidable;
import com.gravity.physics.CollisionEngine;
import com.gravity.physics.PhysicalState;
import com.gravity.physics.RectCollision;

public class PlayerKeyedTile extends AbstractEntity implements Renderer, Resetable {

    private final float TIME_TO_GTFO = 1000;
    private final float TRANSITION_TIME = 500f;
    private final Rect shape;

    private final CollisionEngine collider;
    private final TileRenderer renderer;
    private final TileRenderer yellowRenderer;
    private final TileRenderer blueRenderer;
    private final TileRenderer warningYellowRenderer;
    private final TileRenderer warningBlueRenderer;
    private final TileRenderer warningBothRenderer;

    private boolean exists = true;

    private final Layer layer;
    private final int x, y;
    private final int originalTileId;

    private Player keyedPlayer;
    private float millisElapsedToGTFO = 0f, millisSinceKeyed = 0f;
    private boolean ticking;

    public PlayerKeyedTile(Rect shape, CollisionEngine collider, TileRenderer renderer, TileRenderer yellowRenderer, TileRenderer blueRenderer,
            TileRenderer warningYellowRenderer, TileRenderer warningBlueRenderer, TileRenderer warningBothRenderer, Layer layer, int x, int y) {
        super(new PhysicalState(shape, 0f, 0f, 0f));
        this.shape = shape;
        this.keyedPlayer = null;
        this.collider = collider;
        this.layer = layer;
        this.x = x;
        this.y = y;
        originalTileId = layer.getTileID(x, y);
        this.renderer = renderer;
        this.yellowRenderer = yellowRenderer;
        this.blueRenderer = blueRenderer;
        this.warningYellowRenderer = warningYellowRenderer;
        this.warningBlueRenderer = warningBlueRenderer;
        this.warningBothRenderer = warningBothRenderer;
    }

    @Override
    public void finishUpdate(float millis) {
        // HACK: should also be in controller for this class, like I'm interested enough to care -DX
        if (millisElapsedToGTFO > TIME_TO_GTFO) {
            collider.removeCollidable(this);
            layer.setTileID(x, y, 0);
            exists = false;
        }
        // END HACK
    }

    @Override
    public void startUpdate(float millis) {
        if (ticking) {
            millisElapsedToGTFO += millis;
        }
        if (keyedPlayer != null) {
            millisSinceKeyed += millis;
        }
    }

    @Override
    public void handleCollisions(float millis, Collection<RectCollision> collection) {
        for (RectCollision coll : collection) {
            Collidable other = coll.getOtherEntity(this);
            if (other instanceof Player) {
                if (keyedPlayer == null) {
                    keyedPlayer = (Player) other;
                    // HACK: Should be in a controller class
                    TileType tileType;
                    try {
                        if (keyedPlayer.getName().equals("pink")) {
                            tileType = TileType.PLAYER_KEYED_BLUE;
                        } else {
                            tileType = TileType.PLAYER_KEYED_YELLOW;
                        }
                        layer.setTile(x, y, tileType.tileSetX, tileType.tileSetY, tileType.tileSet);
                    } catch (SlickException e) {
                        throw new RuntimeException(e);
                    }
                    // END HACK
                } else if (other != keyedPlayer) {
                    ticking = true;
                }
            }
        }
    }

    @Override
    public void rehandleCollisions(float millis, Collection<RectCollision> collisions) {
        // No-op
    }

    @Override
    public boolean causesCollisionsWith(Collidable other) {
        if (keyedPlayer != null && other != keyedPlayer) {
            return millisElapsedToGTFO < TIME_TO_GTFO;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PlayerKeyedTile [shape=" + shape + ", keyedPlayer=" + keyedPlayer + ", millisElapsed=" + millisElapsedToGTFO + ", ticking=" + ticking
                + "]";
    }

    @Override
    public void reset() {
        ticking = false;
        millisElapsedToGTFO = Float.NEGATIVE_INFINITY;
        layer.setTileID(x, y, originalTileId);
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        if (!exists) {
            return;
        } else if (ticking) {
            if (millisSinceKeyed < TRANSITION_TIME) {
                warningBothRenderer.render(g, offsetX, offsetY, shape, millisElapsedToGTFO / TIME_TO_GTFO);
            } else if (keyedPlayer.getName().equals("yellow")) {
                warningYellowRenderer.render(g, offsetX, offsetY, shape, millisElapsedToGTFO / TIME_TO_GTFO);
            } else {
                warningBlueRenderer.render(g, offsetX, offsetY, shape, millisElapsedToGTFO / TIME_TO_GTFO);
            }
        } else if (keyedPlayer == null) {
            renderer.render(g, offsetX, offsetY, shape);
        } else if (keyedPlayer.getName().equals("pink")) {
            blueRenderer.render(g, offsetX, offsetY, shape, Math.min(1, millisSinceKeyed / TRANSITION_TIME));
        } else if (keyedPlayer.getName().equals("yellow")) {
            yellowRenderer.render(g, offsetX, offsetY, shape, Math.min(1, millisSinceKeyed / TRANSITION_TIME));
        }
    }

    @Override
    public void unavoidableCollisionFound() {
        // no-op
    }
}
