package com.gravity.levels;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.gravity.geom.Rect;

class CageRenderer implements Renderer {
    private final Image image;
    private final String label;
    private final float x, y;

    /**
     * Create a cage renderer to render a cage at the specified location
     * 
     * @param x
     *            the center x of the cage's position
     * @param y
     *            the bottom y of the cage's position
     * @param label
     *            the label for the cage
     * @throws SlickException
     *             if image could not be loaded
     */
    public CageRenderer(float x, float y, String label) throws SlickException {
        this.image = new Image("assets/frontCage.png");
        this.x = x - image.getWidth() / 2f;
        this.y = y - image.getHeight();
        this.label = label;
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        g.drawImage(image, offsetX + x, offsetY + y);
        g.drawString(label, offsetX + x + 32, offsetY + y + 4);
    }

    public Rect getRect() {
        return new Rect(this.x, this.y, image.getWidth(), image.getHeight());
    }
}