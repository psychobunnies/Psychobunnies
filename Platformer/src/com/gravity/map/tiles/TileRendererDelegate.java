package com.gravity.map.tiles;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.tiled.Tile;
import org.newdawn.slick.tiled.TiledMapPlus;

import com.gravity.geom.Rect;
import com.gravity.map.TileType;

/**
 * Delegate class which will draw a tile at a location for you.
 * 
 * To use, have your tile extend Renderer and take a TileRendererDelegate in its constructor. Delegate the render call to this class.
 * 
 * @author xiao
 * 
 */
public class TileRendererDelegate {
    private Image tileImage;

    public TileRendererDelegate(TiledMapPlus map, TileType type) {
        tileImage = type.getImage(map);
    }

    public TileRendererDelegate(TiledMapPlus map, Tile tile) {
        tileImage = TileType.getImage(map, tile);
    }

    public TileRendererDelegate(TiledMapPlus map, int x, int y, int layerIndex) {
        tileImage = TileType.getImage(map, x, y, layerIndex);
    }

    public void render(Graphics g, int offsetX, int offsetY, Rect location) {
        g.drawImage(tileImage, offsetX + location.getX(), offsetY + location.getY());
    }

}
