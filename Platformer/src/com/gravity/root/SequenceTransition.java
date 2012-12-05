package com.gravity.root;

import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.Transition;

import com.google.common.collect.Lists;

public class SequenceTransition implements Transition {
    
    private List<Transition> transitions;
    
    public SequenceTransition() {
        transitions = Lists.newArrayList();
    }
    
    public void addTransition(Transition t) {
        transitions.add(t);
    }

    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) throws SlickException {
        for (Transition t : transitions) {
            if (!t.isComplete()) {
                t.update(game, container, delta);
                break;
            }
        }
    }

    @Override
    public void preRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
        for (Transition t : transitions) {
            if (!t.isComplete()) {
                t.preRender(game, container, g);
                break;
            }
        }
    }

    @Override
    public void postRender(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
        for (Transition t : transitions) {
            if (!t.isComplete()) {
                t.postRender(game, container, g);
                break;
            }
        }
    }

    @Override
    public boolean isComplete() {
        for (Transition t : transitions) {
            if (!t.isComplete()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void init(GameState firstState, GameState secondState) {
        for (Transition t : transitions) {
            t.init(firstState, secondState);
        }
    }

}
