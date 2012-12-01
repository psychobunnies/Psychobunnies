package com.gravity.fauna;

import java.util.Collection;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.gravity.levels.GameplayControl;
import com.gravity.levels.Renderer;
import com.gravity.levels.UpdateCycling;

/**
 * The class for our wall of death; everything to the left of it will die.
 * 
 * @author xiao
 */
public class WallofDeath implements Renderer, UpdateCycling {

    private final Collection<Player> players;
    private final GameplayControl control;
    private final Image image;

    private final float HEIGHT;
    private final float DELAY;
    private final float imageHeight;
    private final float velX;
    private float posX;
    private float delay;

    /**
     * Contructor.
     * 
     * @param startDelay
     *            the number of millis till the wall starts moving
     * @param startX
     *            the starting x position of the wall in pixels
     * @param startVel
     *            pixels per second movement of the wall
     * @param players
     *            the players that will be subject to the wall
     * @param control
     *            the GameplayControl to signal on death
     */
    public WallofDeath(float startDelay, float startX, float startVel, Collection<Player> players, GameplayControl control, float screenHeight) {
        HEIGHT = screenHeight;
        DELAY = startDelay;
        posX = startX;
        velX = startVel;
        delay = 0;
        this.players = players;
        this.control = control;
        try {
            image = new Image("assets/wallofdeath.png");
            imageHeight = image.getHeight();
        } catch (SlickException e) {
            throw new RuntimeException("Could not load wall of death image", e);
        }
    }

    @Override
    public void startUpdate(float millis) {
        if (delay < DELAY) {
            delay += millis;
        } else {
            posX += millis * velX;
        }
    }

    @Override
    public void finishUpdate(float millis) {
        for (Player player : players) {
            if (player.getPhysicalState().getPosition().x < posX) {
                control.playerDies(player);
            }
        }
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        float x = posX - image.getWidth() + offsetX;
        for (float y = offsetY; y < HEIGHT + offsetY; y += imageHeight) {
            g.drawImage(image, (int) x, y);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WallofDeath [players=");
        builder.append(players);
        builder.append(", control=");
        builder.append(control);
        builder.append(", DELAY=");
        builder.append(DELAY);
        builder.append(", velX=");
        builder.append(velX);
        builder.append(", posX=");
        builder.append(posX);
        builder.append(", delay=");
        builder.append(delay);
        builder.append("]");
        return builder.toString();
    }
}
