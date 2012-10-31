package com.gravity.root;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * 
 * @author dxiao
 */
public class PlatformerGame extends StateBasedGame {

    //@formatter:off
    private LevelInfo[] levels = {
            new LevelInfo("Level 1", "Staircases are hard!", "assets/Levels/game_map_final.tmx", 1001),
            new LevelInfo("Level 2", "More traditional Mario", "assets/Levels/level2.tmx", 1002),
            new LevelInfo("Slingshot", "Slingshot Turorial", "assets/Levels/slingshot_intro.tmx", 1003)
    };
    //@formatter:on

    public PlatformerGame() {
        super("Psychic Psycho Bunnies v1.0");
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        addState(new MainMenuState());
        addState(new LevelSelectState(levels));
        for (LevelInfo level : levels) {
            addState(new GameplayState(level.mapfile, level.stateId));
        }
        addState(new CreditsState());
        addState(new GameOverState());
        addState(new GameWinState());
    }

    public static void main(String args[]) throws SlickException {
        AppGameContainer app = new AppGameContainer(new PlatformerGame());

        app.setDisplayMode(1024, 768, false);
        app.setMaximumLogicUpdateInterval(100);
        app.setMinimumLogicUpdateInterval(10);
        app.setTargetFrameRate(60);
        // app.setSmoothDeltas(true);

        app.start();

    }
}
