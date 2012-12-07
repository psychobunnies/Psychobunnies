package com.gravity.root;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.CrossStateTransition;

import com.gravity.geom.Rect.Side;

public class SlideTransition extends CrossStateTransition {

    private float ratio = 0f;
    private final Side side;
    private final int length;

    public SlideTransition(GameState secondState, Side side, int length) {
        super(secondState);
        
        this.side = side;
        this.length = length;
    }

    @Override
    public void init(GameState firstState, GameState secondState) {
        // no-op
    }

    @Override
    public boolean isComplete() {
        return ratio >= 0.9999f;
    }
    
    @Override
    public void preRenderFirstState(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
        super.preRenderFirstState(game, container, g);
        
        g.pushTransform();
        switch (side) {
        case TOP:
            g.translate(0, -ratio * PlatformerGame.HEIGHT);
            break;
        case LEFT:
            g.translate(-ratio * PlatformerGame.WIDTH, 0);
            break;
        case BOTTOM:
            g.translate(0, ratio * PlatformerGame.HEIGHT);
            break;
        case RIGHT:
            g.translate(ratio * PlatformerGame.WIDTH, 0);
            break;
        default:
            break;
        }
    }

    @Override
    public void preRenderSecondState(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
        g.popTransform();
        
        super.preRenderSecondState(game, container, g);
        
        g.pushTransform();
        switch (side) {
        case TOP:
            g.translate(0, (1 - ratio) * PlatformerGame.HEIGHT);
            break;
        case LEFT:
            g.translate((1 - ratio) * PlatformerGame.WIDTH, 0);
            break;
        case BOTTOM:
            g.translate(0, (ratio - 1) * PlatformerGame.HEIGHT);
            break;
        case RIGHT:
            g.translate((ratio - 1) * PlatformerGame.WIDTH, 0);
            break;
        default:
            break;
        }
    }

    @Override
    public void postRenderSecondState(StateBasedGame game, GameContainer container, Graphics g) throws SlickException {
        super.postRenderSecondState(game, container, g);
    }
    
    @Override
    public void update(StateBasedGame game, GameContainer container, int delta) {
        ratio = (float) Math.min(ratio + ((float)delta) / length, 1.0); 
    }

}
