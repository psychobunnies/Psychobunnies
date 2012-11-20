package com.gravity.map.tiles;

import java.util.List;
import java.util.Map;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.Layer;

import com.gravity.map.TileWorld;
import com.gravity.root.Renderer;

/**
 * Renders a moving collidable.
 */

public class MovingCollidableRenderer implements Renderer {
    private TileWorld world;
    private Layer layer;

    public MovingCollidableRenderer(TileWorld world, Layer layer) {
        //System.out.println("making MCR");
        this.world = world;
        this.layer = layer;
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        List<MovingCollidable> movingColls = world.getMovingCollMap().get(layer);
        MovingCollidable canonicalMovingColl = movingColls.get(0);
        int tileWidth = canonicalMovingColl.getTileWidth();
        int tileHeight = canonicalMovingColl.getTileHeight();
        Vector2f moveOffset = canonicalMovingColl.getPosition(0).sub(
                canonicalMovingColl.getOrigPosition());

        //System.out.println("rendering MC at (" + (moveOffset.x + offsetX) + ", " + (moveOffset.y + offsetY) + ")");
        for (int ty = 0; ty < world.getHeight(); ty++) {
            layer.render((int)(offsetX + moveOffset.x), (int)(offsetY + moveOffset.y), 0, 0,
                    world.getWidth(), ty, false, tileWidth, tileHeight);
        }
    }
}
