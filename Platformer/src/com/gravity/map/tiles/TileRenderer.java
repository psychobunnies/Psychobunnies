package com.gravity.map.tiles;

import org.newdawn.slick.Graphics;

import com.gravity.geom.Rect;

public interface TileRenderer {

    public void render(Graphics g, int offsetX, int offsetY, Rect location);

    // progress moves from 0 to 1, indicating progress of animation
    public void render(Graphics g, int offsetX, int offsetY, Rect location, float progress);

}
