package com.gravity.levels;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class PauseTextRenderer implements UpdateCycling, Renderer {
    
    public static final String text = "Press Esc to pause.";
    public static final int appearLength = 4000;
    public static final int fadeLength = 1000;
    
    private static UnicodeFont font;
    private float timePassed = 0;
    
    static {
        Font awtFont = new Font("SansSerif", Font.PLAIN, 18);
        font = new UnicodeFont(awtFont);
        font.getEffects().add(new ColorEffect(java.awt.Color.white));
        font.addAsciiGlyphs();
        try {
            font.loadGlyphs();
        } catch (SlickException e) {
            throw new RuntimeException("Unable to load font for TriggeredTexts", e);
        }
    }
    
    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        g.setFont(font);
        if (timePassed >= appearLength) {
            g.setColor(new Color(1.0f, 1.0f, 1.0f, Math.max(1.0f - (timePassed - appearLength) / fadeLength, 0f)));
        }
        if (timePassed < appearLength + fadeLength) {
            g.drawString(text, offsetX + 512 - font.getWidth(text) / 2, offsetY + 100);
        }
    }

    @Override
    public void finishUpdate(float millis) {
        timePassed += millis;
    }

    @Override
    public void startUpdate(float millis) {
        // no-op
    }

}
