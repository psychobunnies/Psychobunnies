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
            new LevelInfo("Tutorial", "Controls Turorial", "assets/Levels/tutorial.tmx", 1000),
            new LevelInfo("Slingshot", "Slingshot Turorial", "assets/Levels/slingshot_intro.tmx", 1001),
            new LevelInfo("Split World", "Slingshot Fun", "assets/Levels/split_world.tmx", 1002),
            new LevelInfo("Test Moving", "", "assets/Levels/moving_test.tmx", 1005),
            new LevelInfo("Bouncy 1", "A first brush with bouncing", "assets/Levels/Bouncy_1.tmx", 1006),
    };
    //@formatter:on

    public PlatformerGame() {
        super("Psychic Psycho Bunnies v1.1");
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        addState(new MainMenuState());
        addState(new LevelSelectState(levels));
        for (LevelInfo level : levels) {
            addState(new GameplayState(level.title, level.mapfile, level.stateId));
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
