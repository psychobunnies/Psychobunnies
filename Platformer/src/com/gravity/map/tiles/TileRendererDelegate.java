package com.gravity.map.tiles;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.tiled.TileSet;
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
        TileSet tileSet = map.getTileSet(map.getTilesetID(type.tileSet));
        try {
            tileImage = tileSet.tiles.getSubImage(type.tileSetX, type.tileSetY);
        } catch (Exception e) {
            System.err.println("e");
        }
    }

    public void render(Graphics g, int offsetX, int offsetY, Rect location) {
        g.drawImage(tileImage, offsetX + location.getX(), offsetY + location.getY());
    }

}
