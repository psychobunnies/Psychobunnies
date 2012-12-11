package com.gravity.levels;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.gravity.fauna.Player;
import com.gravity.fauna.Player.Movement;

public class IntroLevelState extends GameplayState {

    public final int destinationId;

    public IntroLevelState(LevelInfo info, int destinationId) throws SlickException {
        super(info);
        this.destinationId = destinationId;
    }

    @Override
    public void keyPressed(int key, char c) {
        if (!controllerA.handleKeyPress(key)) {
            controllerB.handleKeyPress(key);
        }
        if (c == '*') { // HACK: testing purposes only REMOVE FOR RELEASE
            stateWin();
        }
    }

    @Override
    public void playerFinishes(Player player) {
        if (finishedPlayer == null) {
            finishedPlayer = player;
        } else if (finishedPlayer != player) {
            stateWin();
        }
    }

    @Override
    protected void stateWin() {
        done = true;
        if (!finished) {
            reset();
            map.reset();
            finished = true;
            playerA.move(Movement.STOP);
            playerB.move(Movement.STOP);
            try {
                game.getState(destinationId).init(container, game);
            } catch (SlickException e) {
                throw new RuntimeException(e);
            }
            game.enterState(destinationId, new FadeOutTransition(), new FadeInTransition());
        }
    }

}
