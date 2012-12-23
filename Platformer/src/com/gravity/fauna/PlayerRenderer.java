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
    private Image bunnyPlayerRight;
    private Image bunnyPlayerLeft;
    private Image risingBunnyLeft;
    private Image risingBunnyRight;
    private Image fallingBunnyLeft;
    private Image fallingBunnyRight;
    private Image lastImage;
    private List<Image> runningBunny;
    private List<Image> runningBackBunny;
    private int tweener;
    private int counter = 0;

    private static final int DIRECTIONAL_OFFSET = 4;

    public PlayerRenderer(Player player) {
        this.player = player;
        try {
            runningBunny = Lists.newArrayList();
            runningBackBunny = Lists.newArrayList();

            if (player.getName().equals("pink")) {
                bunnyPlayerRight = new Image("./new-assets/bunny/standing-blue.png");
                bunnyPlayerLeft = new Image("./new-assets/bunny/standing-blue-left.png");
                risingBunnyLeft = new Image("./new-assets/bunny/rising-blue-left.png");
                risingBunnyRight = new Image("./new-assets/bunny/rising-blue-right.png");
                fallingBunnyLeft = new Image("./new-assets/bunny/falling-blue-left.png");
                fallingBunnyRight = new Image("./new-assets/bunny/falling-blue-right.png");
                runningBunny.add(new Image("./new-assets/bunny/run-1-blue.png"));
                runningBunny.add(new Image("./new-assets/bunny/run-2-blue.png"));
                runningBunny.add(new Image("./new-assets/bunny/run-3-blue.png"));
                runningBunny.add(new Image("./new-assets/bunny/run-4-blue.png"));
                runningBackBunny.add(new Image("./new-assets/bunny/run-1-blue-back.png"));
                runningBackBunny.add(new Image("./new-assets/bunny/run-2-blue-back.png"));
                runningBackBunny.add(new Image("./new-assets/bunny/run-3-blue-back.png"));
                runningBackBunny.add(new Image("./new-assets/bunny/run-4-blue-back.png"));
            } else {
                bunnyPlayerRight = new Image("./new-assets/bunny/standing-yellow.png");
                bunnyPlayerLeft = new Image("./new-assets/bunny/standing-yellow-left.png");
                risingBunnyLeft = new Image("./new-assets/bunny/rising-yellow-left.png");
                risingBunnyRight = new Image("./new-assets/bunny/rising-yellow-right.png");
                fallingBunnyLeft = new Image("./new-assets/bunny/falling-yellow-left.png");
                fallingBunnyRight = new Image("./new-assets/bunny/falling-yellow-right.png");
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
        int directionalOffset = 0;
        if (tweener % 6 == 0) {
            // if running, animate
            if (player.isRunning()) {
                if (player.getLastWalkedRight()) {
                    lastImage = runningBunny.get(counter);
                } else if (state.velX < 0) {
                    lastImage = runningBackBunny.get(counter);
                }
                counter++;
                if (counter == runningBunny.size()) {
                    counter = 0;
                }
            }

            // if floating, fall
            else if (player.isFalling()) {
                if (player.getLastWalkedRight()) {
                    lastImage = fallingBunnyRight;
                } else {
                    lastImage = fallingBunnyLeft;
                }
            }

            // if rising, rise
            else if (player.isRising()) {
                if (player.getLastWalkedRight()) {
                    lastImage = risingBunnyRight;
                } else {
                    lastImage = risingBunnyLeft;
                }
            }

            // if standing still, stand still
            else {
                if (player.getLastWalkedRight()) {
                    lastImage = bunnyPlayerRight;
                } else {
                    lastImage = bunnyPlayerLeft;
                }
            }
        }

        directionalOffset = player.getLastWalkedRight() ? DIRECTIONAL_OFFSET : -DIRECTIONAL_OFFSET;
        Vector2f pos = state.getPosition();
        g.drawImage(lastImage, pos.x + offsetX - Player.BASE_SHAPE.getX() - directionalOffset, pos.y + offsetY - Player.BASE_SHAPE.getY());
        tweener++;
        //@formatter:off
        // if we ever need to draw hitboxes again:
        //g.pushTransform();
        //g.translate(offsetX, offsetY);
        //g.setColor(Color.red);
        //g.draw(player.getPhysicalState().getRectangle().toShape());
        //g.setColor(Color.green);
        //g.draw(player.getPhysicalState().getRectangle().translate(0, PhysicsFactory.DEFAULT_OFFSET_GROUND_CHECK).toShape());
        //g.setColor(Color.white);
        //g.resetTransform();
        //g.popTransform();
        //@formatter:on
    }
}
