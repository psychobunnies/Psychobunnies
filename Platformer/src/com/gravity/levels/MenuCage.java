package com.gravity.levels;

import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gravity.geom.Rect;

public class MenuCage {
    private final StateBasedGame game;
    private Rect rect;
    private int stateID;

    public MenuCage(StateBasedGame game, float x, float y, int stateID) {
        this.game = game;
        float width = CageRenderer.image.getWidth();
        float height = CageRenderer.image.getHeight();
        this.rect = new Rect(x - width / 2f, y - height, CageRenderer.image.getWidth(), CageRenderer.image.getHeight());
        this.stateID = stateID;
    }

    public Rect getRect() {
        System.err.println("getRect: " + rect);
        return rect;
    }

    public int getToState() {
        return stateID;
    }
    
    public void setToState(int stateID) {
        this.stateID = stateID;
    }

    public boolean intersects(Rect... rects) {
        for (Rect rect : rects) {
            if (!rect.intersects(this.rect)) {
                return false;
            }
        }
        return true;
    }

    public boolean isDisabled() {
        GameState state = game.getState(stateID);
        if (state instanceof GameplayState) {
            return ((GameplayState)state).isFinished();
        }
        return false;
    }
}
