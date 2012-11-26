package com.gravity.root;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.gravity.fauna.Player;
import com.gravity.geom.Rect;

/**
 * The main menu loop.
 * 
 * @author dxiao
 */
public class MainMenuState extends GameplayState {

    static public final int ID = 0;

    private Rectangle startGame;
    private Rectangle credits;

    public MainMenuState() throws SlickException {
        super("Main Menu", "assets/mainmenu.tmx", ID);
    }

    private class CageRenderer implements Renderer {
        private final Image image;
        private final float x, y;

        /**
         * Create a cage renderer to render a cage at the specified location
         * 
         * @param x
         *            the center x of the cage's position
         * @param y
         *            the bottom y of the cage's position
         * @throws SlickException
         *             if image could not be loaded
         */
        public CageRenderer(float x, float y) throws SlickException {
            this.image = new Image("assets/frontCage.png");
            this.x = x - image.getWidth() / 2f;
            this.y = y - image.getHeight();
        }

        @Override
        public void render(Graphics g, int offsetX, int offsetY) {
            g.drawImage(image, offsetX + x, offsetY + y);
        }

        public Rect getRect() {
            return new Rect(this.x, this.y, image.getWidth(), image.getHeight());
        }
    }

    private class MenuCage {
        private Rect rect;
        private int state;

        public MenuCage(Rect rect, int state) {
            this.rect = rect;
            this.state = state;
        }

        public Rect getRect() {
            return rect;
        }

        public int getToState() {
            return state;
        }

        public boolean intersects(Rect... rects) {
            for (Rect rect : rects) {
                if (!rect.intersects(this.rect)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        super.init(container, game);
    }

    @Override
    public void reloadGame() throws SlickException {
        super.reloadGame();

        Vector2f quitLoc = map.getQuitLocation();
        Vector2f optLoc = map.getOptionsLocation();

        CageRenderer quitRend = new CageRenderer(quitLoc.x, quitLoc.y);
        CageRenderer optRend = new CageRenderer(optLoc.x, optLoc.y);
        MenuCage quitCage = new MenuCage(quitRend.getRect(), this.ID);
        MenuCage optCage = new MenuCage(optRend.getRect(), CreditsState.ID);
        renderers.add(quitRend);
        renderers.add(optRend);

    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        if (startGame.contains(x, y)) {
            try {
                game.getState(LevelSelectState.ID).init(container, game);
                game.enterState(LevelSelectState.ID);
            } catch (SlickException e) {
                throw new RuntimeException(e);
            }
        } else if (credits.contains(x, y)) {
            try {
                game.getState(CreditsState.ID).init(container, game);
                game.enterState(CreditsState.ID);
            } catch (SlickException e) {
                throw new RuntimeException(e);
            }
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
}
