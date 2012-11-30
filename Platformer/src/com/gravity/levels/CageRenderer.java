package com.gravity.levels;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;

import com.gravity.geom.Rect;

public class CageRenderer implements Renderer {
    private final Image image;
    private final String label;
    private final UnicodeFont font;
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
    public CageRenderer(float x, float y, String label, UnicodeFont font) throws SlickException {
        this.image = new Image("assets/frontCage.png");
        this.x = x - image.getWidth() / 2f;
        this.y = y - image.getHeight();
        this.font = font;
        this.label = label;
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        g.setFont(font);
        g.drawImage(image, offsetX + x, offsetY + y);
        g.drawString(label, offsetX + x + 32, offsetY + y + 4);
        font.drawString(offsetX + x + 32, offsetY + y + 4, label);
    }

    public Rect getRect() {
        return new Rect(this.x, this.y, image.getWidth(), image.getHeight());
    }
}
