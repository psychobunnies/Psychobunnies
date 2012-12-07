package com.gravity.map.tiles;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.tiled.Tile;
import org.newdawn.slick.tiled.TiledMapPlus;

import com.gravity.geom.Rect;
import com.gravity.map.TileType;

public final class DisappearingTileRenderer implements TileRenderer {

    private Image tileImage;

    public DisappearingTileRenderer(TiledMapPlus map, TileType type) {
        tileImage = type.getImage(map);
    }

    public DisappearingTileRenderer(TiledMapPlus map, Tile tile) {
        tileImage = TileType.getImage(map, tile);
    }

    public DisappearingTileRenderer(TiledMapPlus map, int x, int y, int layerIndex) {
        tileImage = TileType.getImage(map, x, y, layerIndex);
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY, Rect location) {
        render(g, offsetX, offsetY, location, 0);
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY, Rect location, float progress) {
        float alpha = tileImage.getAlpha();
        tileImage.setAlpha((float) Math.sin(progress * Math.PI));
        g.drawImage(tileImage, offsetX + location.getX(), offsetY + location.getY());
        tileImage.setAlpha(alpha);
    }

}
