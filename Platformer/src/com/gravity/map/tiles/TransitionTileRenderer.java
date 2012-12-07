package com.gravity.map.tiles;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.tiled.Tile;
import org.newdawn.slick.tiled.TiledMapPlus;

import com.gravity.geom.Rect;
import com.gravity.map.TileType;

public final class TransitionTileRenderer implements TileRenderer {

    private Image tileImageNormal;
    private Image tileImageNext;

    public TransitionTileRenderer(TiledMapPlus map, TileType typeNormal, TileType typeNext) {
        tileImageNormal = typeNormal.getImage(map);
        tileImageNext = typeNext.getImage(map);
    }

    public TransitionTileRenderer(TiledMapPlus map, Tile tileNormal, Tile tileNext) {
        tileImageNormal = TileType.getImage(map, tileNormal);
        tileImageNext = TileType.getImage(map, tileNext);
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY, Rect location) {
        render(g, offsetX, offsetY, location, 0);
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY, Rect location, float progress) {
        float origAlphaNormal = tileImageNormal.getAlpha();
        float origAlphaNext = tileImageNext.getAlpha();
        float alpha = (float) Math.sin(progress * Math.PI);
        tileImageNormal.setAlpha(Math.min(1f, Math.max(0f, alpha)));
        tileImageNext.setAlpha(Math.min(1, Math.max(0f, 1 - alpha)));
        g.drawImage(tileImageNext, offsetX + location.getX(), offsetY + location.getY());
        g.drawImage(tileImageNormal, offsetX + location.getX(), offsetY + location.getY());
        tileImageNormal.setAlpha(origAlphaNormal);
        tileImageNext.setAlpha(origAlphaNext);
    }

}
