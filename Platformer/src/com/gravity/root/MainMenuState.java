package com.gravity.root;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.TiledMapPlus;

import com.google.common.collect.Lists;
import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.levels.CageRenderer;
import com.gravity.levels.LevelInfo;
import com.gravity.levels.MenuCage;
import com.gravity.levels.Renderer;
import com.gravity.map.LevelFinishZone;
import com.gravity.physics.LayeredCollisionEngine;

/**
 * The main menu loop.
 * 
 * @author dxiao
 */
public class MainMenuState extends CageSelectState {

    static public final int ID = 50;
    static private Rect finalWinBox = new Rect(31 * 32, 20 * 32, 1 * 32, 3 * 32);

    private final LevelInfo[] levels;
    private LevelFinishZone finalWinColl;

    public MainMenuState(LevelInfo[] levels) throws SlickException {
        super(new LevelInfo("Main Menu", "assets/mainmenu2.tmx", ID));
        this.levels = levels;
        finalWinColl = new LevelFinishZone(finalWinBox, this);
    }

    @Override
    public void reloadGame() {
        GameSounds.playMenuMusic();
        super.reloadGame();
    }

    @Override
    protected CagesAndRenderers constructCagesAndRenderers() {
        List<MenuCage> cages = Lists.newLinkedList();
        List<Renderer> renderers = Lists.newLinkedList();

        Vector2f quitLoc = map.getQuitLocation();
        Vector2f optLoc = map.getOptionsLocation();
        Vector2f helpLoc = map.getHelpLocation();

        MenuCage quitCage = new MenuCage(game, quitLoc.x, quitLoc.y, GameQuitState.ID);
        MenuCage optCage = new MenuCage(game, optLoc.x, optLoc.y, CreditsState.ID);
        MenuCage helpCage = new MenuCage(game, helpLoc.x, helpLoc.y, PlatformerGame.TUTORIAL1);

        CageRenderer quitRend, optRend, helpRend, levelRend;
        try {
            quitRend = new CageRenderer(quitCage, "Quit Game");
            optRend = new CageRenderer(optCage, "Credits");
            helpRend = new CageRenderer(helpCage, "Help!");
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }

        renderers.add(quitRend);
        renderers.add(optRend);
        renderers.add(helpRend);
        cages.add(quitCage);
        cages.add(optCage);
        cages.add(helpCage);

        SortedSet<Vector2f> levelLocs = map.getLevelLocations();
        LinkedList<Vector2f> reversedLevelLocs = Lists.newLinkedList();
        for (Vector2f loc : levelLocs) {
            reversedLevelLocs.addFirst(loc);
        }
        int i = levelLocs.size();
        for (Vector2f loc : reversedLevelLocs) {
            i--;
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
            MenuCage levelCage = new MenuCage(game, loc.x, loc.y, info.stateId);
            try {
                levelRend = new CageRenderer(levelCage, info.title);
            } catch (SlickException e) {
                throw new RuntimeException(e);
            }
            renderers.add(levelRend);
            cages.add(levelCage);
        }

        return new CagesAndRenderers(cages, renderers);
    }

    @Override
    protected void stateWin() {
        // No-op
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        int numCompleted = 0;
        for (MenuCage cage : cages) {
            if (cage.isDisabled()) {
                numCompleted++;
            }
        }
        // Plus 1 because of tutorials
        if (numCompleted == levels.length + 1) {
            openPortalEndgame();
        }
    }

    private void openPortalEndgame() {
        TiledMapPlus menuMap = map.map;
        Layer layer = menuMap.getLayer("collisions");
        for (int i = 20; i <= 22; i++) {
            layer.data[31][i][0] = -1;
            layer.data[31][i][1] = 0;
            layer.data[31][i][2] = 0;
        }
        menuMap.getLayer("finish").visible = true;

        collider.addCollidable(finalWinColl, LayeredCollisionEngine.FLORA_LAYER);
    }

    @Override
    public void playerFinishes(Player player) {
        if (finishedPlayer == null) {
            finishedPlayer = player;
        } else if (finishedPlayer != player) {
            gotoWinSequence();
        }
    }

    private void gotoWinSequence() {
        enterCageState(PlatformerGame.TUTORIAL1);
    }
}
