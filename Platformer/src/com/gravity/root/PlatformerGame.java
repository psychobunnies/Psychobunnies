package com.gravity.root;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.gravity.levels.GameplayState;
import com.gravity.levels.LevelInfo;
import com.gravity.levels.MainMenuState;

/**
 * Main root class for the entire game. In order to add levels to the game, please add an entry to the levels table below, using the style of previous
 * levels before you. Namely:
 * 
 * <pre>
 * <code>
 * new LevelInfo("Level Title", "Short Description for us", "path/to/level", levelID),
 * </code>
 * </pre>
 * 
 * The level id can be any number greater than 1000 which is not used by another level already. Try to be sane about it and pick the next highest one.
 * ^_^
 * 
 * @author dxiao
 */
public class PlatformerGame extends StateBasedGame {

    //@formatter:off
    private LevelInfo[] levels = {
            new LevelInfo("Tutorial", "Controls Tutorial", "assets/Levels/tutorial.tmx", 0),
            new LevelInfo("Tutorial 2", "Special Blocks Tutorial", "assets/Levels/enemies_tutorial.tmx", 1),
            new LevelInfo("Slingshot", "Slingshot Turorial", "assets/Levels/slingshot_intro.tmx", 2),
            new LevelInfo("Platformer", "", "assets/Levels/platform.tmx", 3),
            new LevelInfo("Moving", "", "assets/Levels/moving.tmx", 4),
            new LevelInfo("Falling", "So you thought you understood gravity...", "assets/Levels/falling.tmx", 5),
            new LevelInfo("Split World", "Slingshot Fun", "assets/Levels/split_world.tmx", 6),
            new LevelInfo("Test Moving", "", "assets/Levels/moving_test.tmx", 7),
            new LevelInfo("Bouncy 1", "A first brush with bouncing", "assets/Levels/Bouncy_1.tmx", 8),
            new LevelInfo("Test Stomps", "", "assets/Levels/checkpointing.tmx", 9),
            new LevelInfo("Checkpointing", "", "assets/Levels/checkpointing.tmx", 10),
            new LevelInfo("Shortcuts", "Timetest", "assets/levels/shortcuts.tmx", 11),
            new LevelInfo("Elevators","","assets/levels/Elevators.tmx",12),
    };
    //@formatter:on

    public PlatformerGame() {
        super("Psychic Psycho Bunnies v1.1");
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        addState(new MainMenuState(levels));
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
