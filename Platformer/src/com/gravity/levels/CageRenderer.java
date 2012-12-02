package com.gravity.levels;

import java.awt.Color;
import java.awt.Font;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.ShadowEffect;

import com.google.common.base.Preconditions;
import com.gravity.geom.Rect;

public class CageRenderer implements Renderer {
    private static final FloatBuffer skew;
    private static UnicodeFont font;

    private final Image image;
    private final String label;
    private final float cageX, cageY;
    private final float fontX, fontY;

    static {
        skew = BufferUtils.createFloatBuffer(16);
        skew.put(0, 1);
        skew.put(5, 1);
        skew.put(10, 1);
        skew.put(15, 1);
        skew.put(4, 2.0f);
        Font awtFont = new Font("Arial", Font.PLAIN, 14);
        font = new UnicodeFont(awtFont);
        font.getEffects().add(new ShadowEffect(Color.black, 2, 2, 0.5f));
        font.getEffects().add(new ColorEffect(Color.white));
        font.addAsciiGlyphs();
        try {
            font.loadGlyphs();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

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
        Preconditions.checkArgument(font != null, "setFont() must be called before this constructor!");
        this.image = new Image("assets/frontCage.png");
        this.cageX = x - image.getWidth() / 2f;
        this.cageY = y - image.getHeight();
        this.fontX = x - font.getWidth(label) / 2;
        this.fontY = cageY + 12 - font.getHeight(label) / 2;
        this.label = label;
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        g.drawImage(image, offsetX + cageX, offsetY + cageY);
        g.setFont(font);
        g.pushTransform();
        GL11.glTranslatef(offsetX + fontX, offsetY + fontY, 0);
        // GL11.glMultMatrix(skew);
        g.drawString(label, 0, 0);
        g.popTransform();
    }

    public Rect getRect() {
        return new Rect(this.cageX, this.cageY, image.getWidth(), image.getHeight());
    }
}
