package com.gravity.fauna;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.google.common.collect.Lists;
import com.gravity.levels.Renderer;
import com.gravity.physics.PhysicalState;

public class PlayerRenderer implements Renderer {
    private Player player;
    private Image bunnyPlayer;
    private Image lastImage;
    private List<Image> runningBunny;
    private List<Image> runningBackBunny;
    //private List<Image> duckingBunny;
    private int tweener;
    private int counter = 0;

    public PlayerRenderer(Player player) {
        this.player = player;
        try {
            runningBunny = Lists.newArrayList();
            runningBackBunny = Lists.newArrayList();
            //duckingBunny = Lists.newArrayList();

            if (player.getName().equals("pink")) {
                bunnyPlayer = new Image("./new-assets/bunny/standing-blue.png");
                runningBunny.add(new Image("./new-assets/bunny/run-1-blue.png"));
                runningBunny.add(new Image("./new-assets/bunny/run-2-blue.png"));
                runningBunny.add(new Image("./new-assets/bunny/run-3-blue.png"));
                runningBunny.add(new Image("./new-assets/bunny/run-4-blue.png"));
                runningBackBunny.add(new Image("./new-assets/bunny/run-1-blue-back.png"));
                runningBackBunny.add(new Image("./new-assets/bunny/run-2-blue-back.png"));
                runningBackBunny.add(new Image("./new-assets/bunny/run-3-blue-back.png"));
                runningBackBunny.add(new Image("./new-assets/bunny/run-4-blue-back.png"));
            } else {
                bunnyPlayer = new Image("./new-assets/bunny/standing-yellow.png");
                runningBunny.add(new Image("./new-assets/bunny/run-1-yellow.png"));
                runningBunny.add(new Image("./new-assets/bunny/run-2-yellow.png"));
                runningBunny.add(new Image("./new-assets/bunny/run-3-yellow.png"));
                runningBunny.add(new Image("./new-assets/bunny/run-4-yellow.png"));
                runningBackBunny.add(new Image("./new-assets/bunny/run-1-yellow-back.png"));
                runningBackBunny.add(new Image("./new-assets/bunny/run-2-yellow-back.png"));
                runningBackBunny.add(new Image("./new-assets/bunny/run-3-yellow-back.png"));
                runningBackBunny.add(new Image("./new-assets/bunny/run-4-yellow-back.png"));
            }

        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        PhysicalState state = player.getPhysicalState();
        if (tweener % 8 == 0) {
            if (player.isRunning()) {
                if (state.velX > 0) {
                    lastImage = runningBunny.get(counter);
                } else if (state.velX < 0) {
                    lastImage = runningBackBunny.get(counter);
                }
            } else {
                lastImage = bunnyPlayer;
            }
            counter++;
            if (counter == runningBunny.size()) {
                counter = 0;
            }
        }

        Vector2f pos = state.getPosition();
        g.drawImage(lastImage, pos.x + offsetX, pos.y + offsetY);
        tweener++;
        //@formatter:off
        /*
         * // if we ever need to draw hitboxes again:
        g.pushTransform();
        g.translate(offsetX, offsetY);
        g.setColor(Color.red);
        g.draw(player.getRect(0).toShape());
        g.setColor(Color.green);
        g.draw(player.getRect(0).translate(0, PhysicsFactory.DEFAULT_OFFSET_GROUND_CHECK).toShape());
        g.setColor(Color.white);
        g.resetTransform();
        g.popTransform();
        */
        //@formatter:on
    }
}
