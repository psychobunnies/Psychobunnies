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
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.google.common.collect.Maps;

public class GameWinState extends BasicGameState {

    public static final int ID = 11;

    private static final String[] NO_TEXT = new String[] { "", "", "", "", "" };
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
    private Image eyesImage;
    private Image pupilsImage;
    private Image nextButton;
    private Image nextButtonHover;
    private Image nextButtonClick;
    private int buttonState; // 0 for none, 1 for hover, 2 for click
    private String[] winText;
    private int mouseOffsetX;
    private int mouseOffsetY;
    private int mouseX;
    private int mouseY;

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;
        this.winImage = new Image("./new-assets/background/level-end-no-eyes.png");
        this.eyesImage = new Image("./new-assets/background/eyes.png");
        this.pupilsImage = new Image("./new-assets/background/pupils.png");
        this.nextButton = new Image("./new-assets/background/next-button.png");
        this.nextButtonHover = new Image("./new-assets/background/next-button-hover.png");
        this.nextButtonClick = new Image("./new-assets/background/next-button-click.png");
        this.buttonState = 0;
        this.winText = NO_TEXT;

        this.mouseOffsetX = (container.getWidth() - PlatformerGame.WIDTH) / 2;
        this.mouseOffsetY = (container.getHeight() - PlatformerGame.HEIGHT) / 2;
        this.mouseX = 400;
        this.mouseY = 100;
    }

    public void setWinText(String text) {
        this.winText = text.split("\\$");
        if (winText.length != 5) {
            winText = NO_TEXT;
            System.err.println("ERROR: cannot use victory text: " + text + " must have 4 $'s for field delimiting!");
        }
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        Vector2f mouse = new Vector2f(mouseX, mouseY);
        Vector2f eyes = new Vector2f(390f, 100f);
        Vector2f eyesCenter = eyes.copy().add(new Vector2f(eyesImage.getWidth(), eyesImage.getHeight()).scale(0.5f));
        Vector2f mouseDelta = eyesCenter.copy().sub(mouse);
        Vector2f pupils = mouseDelta.copy().normalise().scale(-Math.min(mouseDelta.length(), 5.0f)).add(eyes);

        g.setAntiAlias(true);
        g.drawImage(winImage, 0, 0);
        g.drawImage(eyesImage, (pupils.x + eyes.x) / 2, (pupils.y + eyes.y) / 2);
        g.drawImage(pupilsImage, pupils.x, pupils.y);
        g.pushTransform();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslatef(textX, textY, 0);
        GL11.glMultMatrix(skew);
        g.setFont(titleFont);
        g.drawString(winText[0], (width - titleFont.getWidth(winText[0])) / 2, -10);
        g.setFont(headerFont);
        g.drawString(winText[1], 0, 60);
        g.setFont(textFont);
        g.drawString(winText[2], 0, 85);
        g.setFont(headerFont);
        g.drawString(winText[3], 0, 170);
        g.setFont(textFont);
        g.drawString(winText[4], 0, 195);
        GL11.glPopAttrib();
        g.popTransform();
        if (buttonState == 0) {
            g.drawImage(nextButton, 850, 685);
        }
        else if (buttonState == 1) {
            g.drawImage(nextButtonHover, 850, 685);
        }
        else {
            g.drawImage(nextButtonClick, 850, 685);
        }
        //g.drawString("Onwards!\n(Press Enter)", 800, 700);
        restart = new Rectangle(850, 695, 200, 59);// cuts 25 px of x, 10 px off top and bottom y. Actual image (about) 225 x 79
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        mouseX = newx - mouseOffsetX;
        mouseY = newy - mouseOffsetY;
        if (restart.contains(mouseX, mouseY)) {
            buttonState = 1;
        }
        else {
            buttonState = 0;
        }
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        if (restart.contains(x - mouseOffsetX, y - mouseOffsetY)) {
            buttonState = 2;
            game.enterState(MainMenuState.ID, new FadeOutTransition(), new FadeInTransition());
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == Input.KEY_RETURN) {
            game.enterState(MainMenuState.ID, new FadeOutTransition(), new FadeInTransition());
        }
    }

    @Override
    public int getID() {
        return ID;
    }

}
