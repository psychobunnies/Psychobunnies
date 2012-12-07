package com.gravity.levels;

import org.newdawn.slick.state.StateBasedGame;

public class ResumeCage extends MenuCage {

    public ResumeCage(StateBasedGame game, float x, float y, int stateID) {
        super(game, x, y, stateID);
    }

    public void setToState(int stateID) {
        this.stateID = stateID;
    }
    
    public boolean isDiabeld() {
        return false;
    }

}
