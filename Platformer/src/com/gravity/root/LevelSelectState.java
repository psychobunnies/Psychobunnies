package com.gravity.root;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Really simple and stupid level selection screen - please polish this at some point!
 * 
 * @author xiao
 * 
 */
public class LevelSelectState extends BasicGameState {

    static final int ID = 6;

    static final int TILE_SPACE = 20;
    static final int TILE_WIDTH = 180;
    static final int TILE_HEIGHT = 60;
    static final int GALLERY_X = 32;
    static final int GALLERY_Y = 80;
    static final int GALLERY_HEIGHT = 8;
    static final int GALLERY_WIDTH = 4;
    static final int TITLE_X = 32;
    static final int TITLE_Y = 40;
    static final int BACK_X = 832;
    static final int BACK_Y = 32;
    static final int BACK_HEIGHT = 24;
    static final int BACK_WIDTH = 160;

    private GameContainer container;
    private StateBasedGame game;
    private LevelInfo[] levels;

    public LevelSelectState(LevelInfo[] levels) {
        this.levels = levels;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.container = container;
        this.game = game;
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.drawString("LEVEL SELECT:", TITLE_X, TITLE_Y);
        g.drawRect(BACK_X, BACK_Y, BACK_WIDTH, BACK_HEIGHT);
        g.drawString("Back to Menu", BACK_X + 16, BACK_Y + 6);

        for (int i = 0; i < levels.length; i++) {
            int x = GALLERY_X + (i % GALLERY_WIDTH) * (TILE_WIDTH + TILE_SPACE);
            int y = GALLERY_Y + (i / GALLERY_WIDTH) * (TILE_HEIGHT + TILE_SPACE);
            g.drawRect(x, y, TILE_WIDTH, TILE_HEIGHT);
            g.drawString(levels[i].title, x + 8, y + 8);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        // No-op
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        for (int i = 0; i < levels.length; i++) {
            int tx = GALLERY_X + (i % GALLERY_WIDTH) * (TILE_WIDTH + TILE_SPACE);
            int ty = GALLERY_Y + (i / GALLERY_WIDTH) * (TILE_HEIGHT + TILE_SPACE);
            if (x > tx && x < tx + TILE_WIDTH && y > ty && y < ty + TILE_HEIGHT) {
                try {
                    game.getState(levels[i].stateId).init(container, game);
                    game.enterState(levels[i].stateId);
                } catch (SlickException e) {
                    System.err.println("Could not load level " + levels[i]);
                }
            }
        }
    }

    @Override
    public int getID() {
        return ID;
    }

}
