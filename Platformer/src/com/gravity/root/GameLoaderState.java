package com.gravity.root;

import java.util.Collection;
import java.util.Iterator;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.Image;
import org.newdawn.slick.Color;

import com.gravity.levels.GameplayState;
import com.gravity.levels.LevelInfo;

public class GameLoaderState extends BasicGameState {

    public static final int ID = 0;

    private StateBasedGame game;
    private GameContainer container;
    private Collection<LevelInfo> levels;
    private int loadState = 0;
    private String loadString = "Talking with the professor...";
    private int maxLogicUpdateInterval;
    private Image splashImage;

    private Iterator<LevelInfo> levelItr;
    private LevelInfo levelInfo;

    public GameLoaderState(Collection<LevelInfo> levels, int maxLogicUpdateInterval) {
        this.levels = levels;
        this.maxLogicUpdateInterval = maxLogicUpdateInterval;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.loadState = 0;
        this.levelItr = levels.iterator();
        this.levelInfo = levelItr.next();
        this.game = game;
        this.container = container;
        this.loadString = "Securing research funding...";
        this.splashImage = new Image("./new-assets/background/splash.png");
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.drawImage(this.splashImage, 0, 0);
        g.setColor(Color.black);
        g.drawString(loadString, 624f, 50f);
    }

    protected void updateLoadString(boolean update) {
        if (update) {
            loadState++;
        }
        loadString += "\n";
        switch (loadState) {
        case 0:
            loadString += "Setting up lab...";
            break;
        case 1:
            loadString += "Building experiment: " + levelInfo.title + "...";
            break;
        case 2:
            loadString += "Hiring lawyers...";
            break;
        case 3:
            loadString += "Carving tombstones...";
            break;
        case 4:
            loadString += "Anticipating lab results...";
            break;
        case 5:
            loadString += "Readying break room...";
            break;
        case 6:
            loadString += "Discussing exit strategies...";
            break;
        case 7:
            loadString += "Starting grad student\nbunny raising pipeline...";
            break;
        case 8:
            loadString += "Opening the lab...";
            break;
        }
    }

    private void addState(GameState state) throws SlickException {
        game.addState(state);
        state.init(container, game);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        boolean nextState = true;
        switch (loadState) {
        case 0:
            addState(new MainMenuState(levels.toArray(new LevelInfo[0])));
            break;
        case 1:
            addState(new GameplayState(levelInfo));
            if (levelItr.hasNext()) {
                levelInfo = levelItr.next();
                nextState = false;
            }
            break;
        case 2:
            addState(new CreditsState());
            break;
        case 3:
            addState(new GameOverState());
            break;
        case 4:
            addState(new GameWinState());
            break;
        case 5:
            addState(new PauseState());
            break;
        case 6:
            addState(new GameQuitState());
            break;
        case 7:
            addState(new RestartGameplayState());
            break;
        case 8:
            @SuppressWarnings("unused")
            // assignment so we can get the classloader to run the static code
            GameSounds.Event event = GameSounds.Event.BOUNCE;
            GameSounds.playBGM();
        default:
            game.enterState(MainMenuState.ID);
        }
        updateLoadString(nextState);
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        container.setMaximumLogicUpdateInterval(0);
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        container.setMaximumLogicUpdateInterval(maxLogicUpdateInterval);
    }

    @Override
    public int getID() {
        return ID;
    }

}
