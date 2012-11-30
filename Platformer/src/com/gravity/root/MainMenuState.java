package com.gravity.root;

import java.util.List;
import java.util.SortedSet;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.google.common.collect.Lists;
import com.gravity.levels.CageRenderer;
import com.gravity.levels.LevelInfo;
import com.gravity.levels.MenuCage;
import com.gravity.levels.Renderer;

/**
 * The main menu loop.
 * 
 * @author dxiao
 */
public class MainMenuState extends CageSelectState {

    static public final int ID = 0;

    private final LevelInfo[] levels;

    public MainMenuState(LevelInfo[] levels) throws SlickException {
        super("Main Menu", "assets/mainmenu2.tmx", ID);
        this.levels = levels;
    }

    @Override
    protected CagesAndRenderers constructCagesAndRenderers() {
        List<MenuCage> cages = Lists.newLinkedList();
        List<Renderer> renderers = Lists.newLinkedList();
        
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
        renderers.add(quitRend);
        renderers.add(optRend);
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
            renderers.add(levelRend);
            cages.add(levelCage);
        }
        
        return new CagesAndRenderers(cages, renderers);
    }

}
