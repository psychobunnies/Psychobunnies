package com.gravity.root;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gravity.levels.MainMenuState;

public class CreditsState extends BasicGameState {
    
    static public final int ID = 5;
    private StateBasedGame game;
    private Rectangle MenuButton;
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.drawString("CREDITS", 50, 75);
        g.drawString("Music by Matthew Pablo", 50, 125);
        g.drawString("Sound effects were made by Zachary Segal", 50, 150);
        g.drawString("Game design by Kevin Yue, David Xiao and Predrag Gruevski", 50, 175);
        g.drawString("Game frameworks were written by Predrag Gruevski, David Xiao and Kevin Yue", 50, 200);
        g.drawString("Maps were made by Chris Dessonville", 50, 225);
        g.drawString("Graphics and rendering designed and implemented by Chris Dessonville and Zachary Segal", 50, 250);
        g.drawString("Assets from 'The Independent Gaming Source's Assemblee Competition' were used", 50, 275);
        g.drawString(" with permission from the authors.", 50, 300);
        
        g.draw(MenuButton = new Rectangle(48, 383, 100, 48));
        g.drawString("Back", 50, 385);
    }
    
    @Override
    public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {
    }
    
    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        if (MenuButton.contains(x, y)) {
            game.enterState(MainMenuState.ID);
        }
    }
    
    @Override
    public int getID() {
        return ID;
    }
    
}
