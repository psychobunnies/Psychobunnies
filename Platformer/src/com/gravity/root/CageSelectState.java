package com.gravity.root;

import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.google.common.collect.Lists;
import com.gravity.fauna.Player;
import com.gravity.fauna.Player.Movement;
import com.gravity.levels.GameplayState;
import com.gravity.levels.LevelInfo;
import com.gravity.levels.MenuCage;
import com.gravity.levels.RenderList;
import com.gravity.levels.Renderer;

public abstract class CageSelectState extends GameplayState {

    private static final int SELECT_KEY = Input.KEY_ENTER;
    private List<MenuCage> cages;
    private int mouseOffsetX;
    private int mouseOffsetY;

    public CageSelectState(LevelInfo info) throws SlickException {
        super(info);

        cages = Lists.newLinkedList();
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        super.init(container, game);
        
        mouseOffsetX = (container.getWidth() - PlatformerGame.WIDTH) / 2;
        mouseOffsetY = (container.getHeight() - PlatformerGame.HEIGHT) / 2;
    }

    public void enterCageState(MenuCage cage) {
        try {
            playerA.move(Movement.STOP);
            playerB.move(Movement.STOP);
            game.getState(cage.getToState()).init(container, game);
            game.enterState(cage.getToState(), new FadeOutTransition(), new FadeInTransition());
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        for (MenuCage cage : cages) {
            if (cage.getRect().contains(x - mouseOffsetX, y - mouseOffsetY)) {
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

        // remove the level finish zone
        collider.removeCollidable(finish);

        game.unpauseRender();
        game.unpauseUpdate();
    }

    @Override
    public boolean canPause() {
        return false;
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
