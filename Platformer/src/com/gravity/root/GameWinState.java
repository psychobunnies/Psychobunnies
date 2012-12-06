package com.gravity.root;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.nio.FloatBuffer;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.google.common.collect.Maps;

public class GameWinState extends BasicGameState {

    public static final int ID = 11;

    private static final FloatBuffer skew;
    private static final UnicodeFont textFont, titleFont, headerFont;
    private static final float textX = 580, textY = 275, width = 250;

    static {
        skew = BufferUtils.createFloatBuffer(16);
        skew.put(0, 0.9911f);
        skew.put(1, -0.1334f);
        skew.put(4, .1843f);
        skew.put(5, 0.9828f);
        skew.put(10, 1);
        skew.put(15, 1);
        Font awtFont = new Font("Monospaced", Font.BOLD, 16);
        textFont = new UnicodeFont(awtFont);
        textFont.getEffects().add(new ColorEffect(Color.black));
        textFont.addAsciiGlyphs();
        awtFont = new Font("Serif", Font.BOLD, 40);
        titleFont = new UnicodeFont(awtFont);
        titleFont.getEffects().add(new ColorEffect(Color.black));
        titleFont.addAsciiGlyphs();
        Map<TextAttribute, Object> attributes = Maps.newHashMap();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        awtFont = new Font("Monospaced", Font.BOLD, 20).deriveFont(attributes);
        headerFont = new UnicodeFont(awtFont);
        headerFont.getEffects().add(new ColorEffect(Color.black));
        headerFont.addAsciiGlyphs();

        try {
            textFont.loadGlyphs();
            titleFont.loadGlyphs();
            headerFont.loadGlyphs();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    private StateBasedGame game;
    private Rectangle restart;
    private Image winImage;
    private String[] winText;

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;
        this.winImage = new Image("./new-assets/background/level-end.png");
        this.winText = new String[] { "", "", "", "", "" };
    }

    public void setWinText(String text) {
        this.winText = text.split("\\$");
        if (winText.length != 5) {
            throw new RuntimeException("ERROR: cannot use victory text: " + text + " must have 4 $'s for field delimiting!");
        }
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setAntiAlias(true);
        g.drawImage(winImage, 0, 0);
        g.pushTransform();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslatef(textX, textY, 0);
        GL11.glMultMatrix(skew);
        g.setFont(titleFont);
        g.drawString(winText[0], (width - titleFont.getWidth(winText[0])) / 2, 0);
        g.setFont(headerFont);
        g.drawString(winText[1], 0, 60);
        g.setFont(textFont);
        g.drawString(winText[2], 0, 85);
        g.setFont(headerFont);
        g.drawString(winText[3], 0, 200);
        g.setFont(textFont);
        g.drawString(winText[4], 0, 225);
        GL11.glPopAttrib();
        g.popTransform();
        g.draw(restart = new Rectangle(798, 698, 200, 48));
        g.drawString("Onwards!\n(Press Enter)", 800, 700);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        if (restart.contains(x, y)) {
            game.enterState(MainMenuState.ID);
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == Input.KEY_RETURN) {
            game.enterState(MainMenuState.ID);
        }
    }

    @Override
    public int getID() {
        return ID;
    }

}
