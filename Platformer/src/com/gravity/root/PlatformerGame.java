package com.gravity.root;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.google.common.collect.Lists;
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
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    
    //@formatter:off
    private LevelInfo[] levels = {

            // Very Hard (1)
            new LevelInfo("Moving", VictoryText.MOVING, "assets/Levels/moving.tmx"),
           
            // Impossible
            new LevelInfo("Falling", VictoryText.FALLING, "assets/Levels/falling.tmx"),
            new LevelInfo("Split World", VictoryText.SLINGSHOT, "assets/Levels/split_world.tmx"),
            
            // Very Hard (1)
            new LevelInfo("Bouncy 2", VictoryText.BOUNCY2, "assets/Levels/Bouncy_2.tmx"),
            
            // Hard (4)
            new LevelInfo("Bouncy 1", VictoryText.BOUNCY1, "assets/Levels/Bouncy_1.tmx"),
            //new LevelInfo("Test Stomps", VictoryText.TEST, "assets/Levels/checkpointing.tmx"),
            //new LevelInfo("Checkpointing",  VictoryText.TEST, "assets/Levels/checkpointing.tmx"),

            // Medium (4)
            new LevelInfo("Elevators", VictoryText.ELEVATORS, "assets/levels/Elevators.tmx"),
            new LevelInfo("Shortcuts", VictoryText.SHORTCUTS, "assets/levels/shortcuts.tmx"),
            new LevelInfo("Slingshot", VictoryText.SLINGSHOT, "assets/Levels/slingshot_intro.tmx"),
            new LevelInfo("Platformer", VictoryText.PLATFORMER, "assets/Levels/platform.tmx"),
            // Easy (2)
            new LevelInfo("Lab Procedures", VictoryText.PROCEDURES, "assets/Levels/intro_tutorial.tmx"),
            new LevelInfo("Lab Safety", VictoryText.SAFETY, "assets/Levels/enemies_tutorial.tmx"),
    };
    //@formatter:on

    public PlatformerGame() {
        super("Psychic Psycho Bunnies v1.1");
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        addState(new GameLoaderState(Lists.newArrayList(levels), 100));
    }
    
    @Override
    protected void preRenderState(GameContainer container, Graphics g) throws SlickException {
        //g.translate((container.getScreenWidth() - 1024) / 2,
        //            (container.getScreenHeight() - 768) / 2);
    }

    public static void main(String args[]) throws SlickException {
        AppGameContainer app = new AppGameContainer(new PlatformerGame());

        app.setDisplayMode(1024, 768, false);
        app.setMaximumLogicUpdateInterval(100);
        app.setMinimumLogicUpdateInterval(10);
        app.setTargetFrameRate(60);
        app.setAlwaysRender(true);
        app.setVSync(true);
        if (app.supportsMultiSample()) {
            app.setMultiSample(4);
        }
        // app.setSmoothDeltas(true);

        boolean isDebugging = false;
        try {
            isDebugging |= java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        isDebugging |= args.length > 0 && args[0].equals("nofullscreen");
        if (!isDebugging) {
            try {
                Display.setFullscreen(true);
                Display.setDisplayMode(Display.getDesktopDisplayMode());
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        }

        app.start();

    }
}
