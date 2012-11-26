package com.gravity.root;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gravity.levels.MainMenuState;

public class GameOverState extends BasicGameState {

    public static final int ID = 10;

    private StateBasedGame game;
    private Rectangle restart;

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.drawString("Game Over! (One of the bunnies fell off the screen! Why would you do that?)", 50, 100);
        g.draw(restart = new Rectangle(48, 148, 200, 48));
        g.drawString("Back to main menu...", 50, 150);
        //@formatter:off
        g.drawString("Hint:\n" +
        		"Those big red and white arrows on your screen?\n" +
        		"Red arrows mean that your controls in that\n" +
        		"direction have been remapped to your partner's,\n" +
        		"and vice versa.\n" +
        		"White arrows mean that they have been remapped\n" +
        		"back to you.", 350, 150);
        //@formatter:on
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
    public int getID() {
        return ID;
    }

}
