package com.gravity.entity;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gravity.levels.Renderer;

public class TriggeredImage extends TriggeredBase implements Renderer {

    public final Image image;

    public TriggeredImage(int x, int y, Image image) {
        super(x, y);
        this.image = image;
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        // g.drawImage(image, offsetX + x, offsetY + y);
        // HACK: Display images at 0,0
        g.drawImage(image, 0, 0);

    }

}
