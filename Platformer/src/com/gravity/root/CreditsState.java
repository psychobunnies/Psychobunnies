package com.gravity.root;

import java.awt.Color;
import java.awt.Font;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.ShadowEffect;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class CreditsState extends BasicGameState {

    static public final int ID = 5;
    private StateBasedGame game;
    private Rectangle MenuButton;
    private UnicodeFont font;
    private Image background;
    private int mouseOffsetX;
    private int mouseOffsetY;

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;
        Font awtFont = new Font("SansSerif", Font.BOLD, 14);
        font = new UnicodeFont(awtFont);
        font.getEffects().add(new ShadowEffect(Color.black, 2, 2, 0.5f));
        font.getEffects().add(new ColorEffect(Color.white));
        font.addAsciiGlyphs();
        font.loadGlyphs();
        background = new Image("new-assets/background/background-no-shelf.png");

        mouseOffsetX = (container.getWidth() - PlatformerGame.WIDTH) / 2;
        mouseOffsetY = (container.getHeight() - PlatformerGame.HEIGHT) / 2;
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.drawImage(background, 0, 0, 0, 0, PlatformerGame.WIDTH, PlatformerGame.HEIGHT);
        g.setFont(font);
        g.drawString("CREDITS", 50, 75);
        g.drawString("Music:", 50, 125);
        g.drawString("Cake Town: Matthew Pablo", 50, 150);
        g.drawString("Almost Out: HorrorPen", 50, 175);
        g.drawString("Sound effects: Even Martinez and Zachary Segal", 50, 200);
        g.drawString("       sound effects from fressound.org, by CGEffex, Sagetyrtle, Patchen, Buzzbox\n" +
        		     "       Plingativator, Streety, Denis Chapon, Setuniman, Morgantj", 50, 220);
        g.drawString("Lead Programmers: Predrag Gruevski, David Xiao and Patrick Hulin", 50, 265);
        g.drawString("Level Design: Kevin Yue, Eli Davis, and Ziad Baaklini", 50, 290);
        g.drawString("Art Assets: Turner Bohlen and Zachary Segal", 50, 315);
        g.drawString("*Special thanks to Chris Dessonville for his work on the original prototype*", 50, 340);

        g.draw(MenuButton = new Rectangle(48, 383, 100, 48));
        g.drawString("Back", 50, 385);
    }

    @Override
    public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        if (MenuButton.contains(x - mouseOffsetX, y - mouseOffsetY)) {
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
