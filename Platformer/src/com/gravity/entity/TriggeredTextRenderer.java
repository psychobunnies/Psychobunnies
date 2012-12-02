package com.gravity.entity;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import com.gravity.levels.Renderer;

/**
 * Renders a TriggeredText.
 * 
 * @author phulin
 */
public class TriggeredTextRenderer implements Renderer {
    private final TriggeredText triggeredText;
    private static UnicodeFont font;

    static {
        Font awtFont = new Font("SansSerif", Font.BOLD, 16);
        font = new UnicodeFont(awtFont);
        font.getEffects().add(new ColorEffect(java.awt.Color.white));
        font.addAsciiGlyphs();
        try {
            font.loadGlyphs();
        } catch (SlickException e) {
            throw new RuntimeException("Unable to load font for TriggeredTexts", e);
        }
    }

    public TriggeredTextRenderer(TriggeredText triggeredText) {
        this.triggeredText = triggeredText;
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        if (triggeredText.isTriggered()) {
            Color c = g.getColor();
            g.setFont(font);
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
