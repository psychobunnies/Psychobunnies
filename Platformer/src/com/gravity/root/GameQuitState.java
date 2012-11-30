package com.gravity.root;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Game state which ends the game and performas any cleanup operations needed.
 * 
 * @author xiao
 */
public class GameQuitState extends BasicGameState {

    public static final int ID = 12;

    private GameContainer container;
    private StateBasedGame game;

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.container = container;
        this.game = game;
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        // No-op
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        container.exit();
    }

    @Override
    public int getID() {
        return ID;
    }

}
