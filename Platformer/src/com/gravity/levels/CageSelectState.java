package com.gravity.levels;

import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.Transition;

import com.google.common.collect.Lists;
import com.gravity.fauna.Player;

public abstract class CageSelectState extends GameplayState {

    private static final int SELECT_KEY = Input.KEY_ENTER;
    private List<MenuCage> cages;
    protected Transition fadeIn;
    protected Transition fadeOut;

    public CageSelectState(String levelName, String mapFile, int id) throws SlickException {
        super(levelName, mapFile, id);
        
        cages = Lists.newLinkedList();
        fadeIn = new FadeInTransition();
        fadeOut = new FadeOutTransition();
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        super.init(container, game);
    }

    public void enterCageState(MenuCage cage) {
        try {
            game.getState(cage.getToState()).init(container, game);
            game.enterState(cage.getToState(), fadeOut, fadeIn);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        for (MenuCage cage : cages) {
            if (cage.getRect().contains(x, y)) {
                enterCageState(cage);
            }
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == SELECT_KEY) {
            for (MenuCage cage : cages) {
                if (cage.intersects(playerA.getPhysicalState().getRectangle(), playerB.getPhysicalState().getRectangle())) {
                    enterCageState(cage);
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
    
    @Override
    public void reloadGame() {
        super.reloadGame();

        game.pauseRender();
        game.pauseUpdate();

        CagesAndRenderers cagesAndRenderers = constructCagesAndRenderers();
        cages.clear();
        cages.addAll(cagesAndRenderers.cages);
        renderers.addAll(cagesAndRenderers.renderers, RenderList.FLORA);

        game.unpauseRender();
        game.unpauseUpdate();
    }
    
    protected class CagesAndRenderers {
        public final List<MenuCage> cages;
        public final List<Renderer> renderers;

        public CagesAndRenderers(List<MenuCage> cages, List<Renderer> renderers) {
            this.cages = cages;
            this.renderers = renderers;
        }
    }

    protected abstract CagesAndRenderers constructCagesAndRenderers();
    
}
