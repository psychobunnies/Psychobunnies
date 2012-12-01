package com.gravity.root;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.gravity.levels.GameplayState;
import com.gravity.levels.LevelInfo;

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
    private int i = -1;
    //@formatter:off
    private LevelInfo[] levels = {

            // Very Hard (1)
            new LevelInfo("Moving", "", "assets/Levels/moving.tmx", ++i),
           
            // Impossible
            new LevelInfo("Falling", "So you thought you understood gravity...", "assets/Levels/falling.tmx", ++i),
            new LevelInfo("Split World", "Slingshot Fun", "assets/Levels/split_world.tmx", ++i),
            
            // Very Hard (1)
            new LevelInfo("Bouncy 2", "", "assets/Levels/Bouncy_2.tmx", ++i),
            
            // Hard (4)
            new LevelInfo("Bouncy 1", "A first brush with bouncing", "assets/Levels/Bouncy_1.tmx", ++i),
            new LevelInfo("Test Stomps", "", "assets/Levels/checkpointing.tmx", ++i),
            new LevelInfo("Checkpointing", "", "assets/Levels/checkpointing.tmx", ++i),
            new LevelInfo("Test Moving", "", "assets/Levels/moving_test.tmx", ++i),


            // Medium (4)
            new LevelInfo("Elevators","","assets/levels/Elevators.tmx", ++i),
            new LevelInfo("Shortcuts", "Timetest", "assets/levels/shortcuts.tmx", ++i),
            new LevelInfo("Slingshot", "Slingshot Turorial", "assets/Levels/slingshot_intro.tmx", ++i),
            new LevelInfo("Platformer", "", "assets/Levels/platform.tmx", ++i),
            
            // Easy (2)
            new LevelInfo("Lab Procedures", "Formerly Tutorial 1", "assets/Levels/tutorial.tmx", ++i),
            new LevelInfo("Lab Safety", "Formerly Tutorial 2", "assets/Levels/enemies_tutorial.tmx", ++i),
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
        addState(new PauseState());
        addState(new GameQuitState());
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
