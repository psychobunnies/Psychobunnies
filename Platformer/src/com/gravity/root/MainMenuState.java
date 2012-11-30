package com.gravity.root;

import java.util.List;
import java.util.SortedSet;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.Transition;

import com.google.common.collect.Lists;
import com.gravity.fauna.Player;
import com.gravity.levels.CageRenderer;
import com.gravity.levels.GameplayState;
import com.gravity.levels.LevelInfo;
import com.gravity.levels.MenuCage;
import com.gravity.levels.RenderList;

/**
 * The main menu loop.
 * 
 * @author dxiao
 */
public class MainMenuState extends GameplayState {

    static public final int ID = 0;

    static private final int LEVEL_SELECT_KEY = Input.KEY_ENTER;

    private final LevelInfo[] levels;

    private List<MenuCage> cages;

    private Transition fadeIn;
    private Transition fadeOut;

    public MainMenuState(LevelInfo[] levels) throws SlickException {
        super("Main Menu", "assets/mainmenu.tmx", ID);
        this.levels = levels;
        fadeIn = new FadeInTransition();
        fadeOut = new FadeOutTransition();
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        super.init(container, game);
    }

    @Override
    public void reloadGame() {
        super.reloadGame();

        game.pauseRender();
        game.pauseUpdate();

        cages = Lists.newLinkedList();

        Vector2f quitLoc = map.getQuitLocation();
        Vector2f optLoc = map.getOptionsLocation();

        CageRenderer quitRend, optRend, levelRend;
        try {
            quitRend = new CageRenderer(quitLoc.x, quitLoc.y, "Quit Game");
            optRend = new CageRenderer(optLoc.x, optLoc.y, "Credits");
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }

        MenuCage quitCage = new MenuCage(quitRend.getRect(), GameQuitState.ID);
        MenuCage optCage = new MenuCage(optRend.getRect(), CreditsState.ID);
        renderers.add(quitRend, RenderList.FLORA);
        renderers.add(optRend, RenderList.FLORA);
        cages.add(quitCage);
        cages.add(optCage);

        SortedSet<Vector2f> levelLocs = map.getLevelLocations();
        int i = -1;
        for (Vector2f loc : levelLocs) {
            i++;
            LevelInfo info = null;
            for (LevelInfo level : levels) {
                if (level.levelOrder == i) {
                    info = level;
                    break;
                }
            }
            if (info == null) {
                continue;
            }
            try {
                levelRend = new CageRenderer(loc.x, loc.y, info.title);
            } catch (SlickException e) {
                throw new RuntimeException(e);
            }
            MenuCage levelCage = new MenuCage(levelRend.getRect(), info.stateId);
            renderers.add(levelRend, RenderList.FLORA);
            cages.add(levelCage);
        }

        collider.removeCollidable(finish);

        game.unpauseRender();
        game.unpauseUpdate();
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        for (MenuCage cage : cages) {
            if (cage.getRect().contains(x, y)) {
                try {
                    game.getState(cage.getToState()).init(container, game);
                    game.enterState(cage.getToState(), fadeOut, fadeIn);
                } catch (SlickException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == LEVEL_SELECT_KEY) {
            for (MenuCage cage : cages) {
                if (cage.intersects(playerA.getPhysicalState().getRectangle(), playerB.getPhysicalState().getRectangle())) {
                    try {
                        game.getState(cage.getToState()).init(container, game);
                        game.enterState(cage.getToState(), fadeOut, fadeIn);
                    } catch (SlickException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            super.keyPressed(key, c);
        }
    }

    @Override
    public void playerDies(Player player) {
        throw new RuntimeException("Player " + player + " just died in the main menu!");
    }

    @Override
    public void playerHitSpikes(Player player) {
        throw new RuntimeException("Player " + player + " just hit spikes in the main menu!");
    }

    @Override
    public void playerFinishes(Player player) {
        throw new RuntimeException("Player " + player + " just found level finish in the main menu!");
    }
}
