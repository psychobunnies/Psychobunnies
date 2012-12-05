package com.gravity.root;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.google.common.collect.Lists;
import com.gravity.levels.CageRenderer;
import com.gravity.levels.GameplayState;
import com.gravity.levels.MenuCage;
import com.gravity.levels.Renderer;

public class PauseState extends CageSelectState {

    public static final int ID = 22;

    private MenuCage resumeCage;

    public PauseState() throws SlickException {
        super("Pause", "assets/pause.tmx", ID);
    }

    @Override
    public void enterCageState(MenuCage cage) {
        game.enterState(cage.getToState(), new FadeOutTransition(Color.black, 400), new FlashTransition(2000, 3));
    }

    @Override
    protected CagesAndRenderers constructCagesAndRenderers() {
        List<MenuCage> cages = Lists.newLinkedList();
        List<Renderer> renderers = Lists.newLinkedList();

        Vector2f resumeLoc = map.getSpecialLocation("resume");
        Vector2f mainMenuLoc = map.getSpecialLocation("mainmenu");
        
        resumeCage = new MenuCage(game, resumeLoc.x, resumeLoc.y, MainMenuState.ID);
        MenuCage mainMenuCage = new MenuCage(game, mainMenuLoc.x, mainMenuLoc.y, MainMenuState.ID);

        CageRenderer resumeRend, mainMenuRend;
        try {
            resumeRend = new CageRenderer(resumeCage, "Resume");
            mainMenuRend = new CageRenderer(mainMenuCage, "Main Menu");
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }

        renderers.add(resumeRend);
        renderers.add(mainMenuRend);
        cages.add(resumeCage);
        cages.add(mainMenuCage);

        return new CagesAndRenderers(cages, renderers);
    }

    public void setGameplayState(GameplayState gameplayState) {
        resumeCage.setToState(gameplayState.getID());
    }

}
