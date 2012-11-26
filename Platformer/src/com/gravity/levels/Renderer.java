package com.gravity.levels;

import org.newdawn.slick.Graphics;

public interface Renderer {

    /**
     * Render the object that this renderer is attached to.
     * 
     * @param g
     *            the graphics object to render to. Should be in the object's coordinate frame.
     * @param offsetX
     * @param offsetY
     */
    public void render(Graphics g, int offsetX, int offsetY);

}
