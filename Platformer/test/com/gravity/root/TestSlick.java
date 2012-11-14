package com.gravity.root;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * 
 * @author dxiao
 */
public class TestSlick extends BasicGame {

    public TestSlick() {
        super("SimpleTest");
    }

    @Override
    public void init(GameContainer container) throws SlickException {
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        container.exit();
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
        g.drawString("Hello, Slick world!", 0, 100);
    }

    public void run() throws SlickException {
        AppGameContainer app = new AppGameContainer(this);
        app.setForceExit(false);
        app.start();
    }

    public static void main(String[] args) {
        try {
            TestSlick game = new TestSlick();
            AppGameContainer app = new AppGameContainer(game);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
