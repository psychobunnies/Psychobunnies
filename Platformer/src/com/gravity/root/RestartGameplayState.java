package com.gravity.root;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import com.gravity.levels.GameplayState;

public class RestartGameplayState extends BasicGameState {
    
    public static final int ID = 33;
    
    private GameplayState state;

    public RestartGameplayState() {
        this.state = null;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException { 
        g.setColor(Color.red.darker());
        g.fillRect(0, 0, container.getScreenWidth(), container.getScreenHeight());
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        state.reset();
        try {
            state.init(container, game);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        game.enterState(state.getID(), null, new FadeInTransition(Color.red.darker(), 300));
    }

    @Override
    public int getID() {
        return ID;
    }
    
    public void setToState(GameplayState state) {
        this.state = state;
    }

}
