package com.gravity.entity;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.gravity.levels.Renderer;

/**
 * Renders a TriggeredText.
 * 
 * @author phulin
 */
public class TriggeredTextRenderer implements Renderer {
    private final TriggeredText triggeredText;

    public TriggeredTextRenderer(TriggeredText triggeredText) {
        this.triggeredText = triggeredText;
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        if (triggeredText.isTriggered()) {
            Color c = g.getColor();
            g.setColor(triggeredText.color);
            //@formatter:off
            g.drawString(triggeredText.text,
                         offsetX + triggeredText.x,
                         offsetY + triggeredText.y);
            //@formatter:on
            g.setColor(c);
        }
    }
}
