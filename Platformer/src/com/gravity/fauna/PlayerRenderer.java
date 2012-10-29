package com.gravity.fauna;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.google.common.collect.Lists;
import com.gravity.physics.PhysicalState;
import com.gravity.root.Renderer;

public class PlayerRenderer implements Renderer {
    private Player player;
    private Image bunnyPlayer;
    private Image lastImage;
    private List<Image> runningBunny;
    private List<Image> runningBackBunny;
    private List<Image> duckingBunny;
    private int tweener;
    private int counter = 0;
    
    public PlayerRenderer(Player player) {
        this.player = player;
        try {
            runningBunny = Lists.newArrayList();
            runningBackBunny = Lists.newArrayList();
            duckingBunny = Lists.newArrayList();
            
            if (player.getName().equals("pink")) {
                bunnyPlayer = new Image("./assets/BunnyAssets/bunnyStand.png");
                runningBunny.add(new Image("./assets/BunnyAssets/bunnyRun1.png"));
                runningBunny.add(new Image("./assets/BunnyAssets/bunnyRun2.png"));
                runningBunny.add(new Image("./assets/BunnyAssets/bunnyRun3.png"));
                runningBunny.add(new Image("./assets/BunnyAssets/bunnyRun4.png"));
                runningBunny.add(new Image("./assets/BunnyAssets/bunnyRun5.png"));
                runningBunny.add(new Image("./assets/BunnyAssets/bunnyRun6.png"));
                duckingBunny.add(new Image("./assets/BunnyAssets/bunnyDuck1.png"));
                duckingBunny.add(new Image("./assets/BunnyAssets/bunnyDuck2.png"));
                duckingBunny.add(new Image("./assets/BunnyAssets/bunnyDuck3.png"));
                runningBackBunny.add(new Image("./assets/BunnyAssets/bunnyRunBack1.png"));
                runningBackBunny.add(new Image("./assets/BunnyAssets/bunnyRunBack2.png"));
                runningBackBunny.add(new Image("./assets/BunnyAssets/bunnyRunBack3.png"));
                runningBackBunny.add(new Image("./assets/BunnyAssets/bunnyRunBack4.png"));
                runningBackBunny.add(new Image("./assets/BunnyAssets/bunnyRunBack5.png"));
                runningBackBunny.add(new Image("./assets/BunnyAssets/bunnyRunBack6.png"));
            } else {
                bunnyPlayer = new Image("./assets/BunnyAssets/yellowbunnyStand.png");
                runningBunny.add(new Image("./assets/BunnyAssets/yellowbunnyRun1.png"));
                runningBunny.add(new Image("./assets/BunnyAssets/yellowbunnyRun2.png"));
                runningBunny.add(new Image("./assets/BunnyAssets/yellowbunnyRun3.png"));
                runningBunny.add(new Image("./assets/BunnyAssets/yellowbunnyRun4.png"));
                runningBunny.add(new Image("./assets/BunnyAssets/yellowbunnyRun5.png"));
                runningBunny.add(new Image("./assets/BunnyAssets/yellowbunnyRun6.png"));
                duckingBunny.add(new Image("./assets/BunnyAssets/bunnyDuck1.png"));
                duckingBunny.add(new Image("./assets/BunnyAssets/bunnyDuck2.png"));
                duckingBunny.add(new Image("./assets/BunnyAssets/bunnyDuck3.png"));
                runningBackBunny.add(new Image("./assets/BunnyAssets/yellowbunnyRunBack1.png"));
                runningBackBunny.add(new Image("./assets/BunnyAssets/yellowbunnyRunBack2.png"));
                runningBackBunny.add(new Image("./assets/BunnyAssets/yellowbunnyRunBack3.png"));
                runningBackBunny.add(new Image("./assets/BunnyAssets/yellowbunnyRunBack4.png"));
                runningBackBunny.add(new Image("./assets/BunnyAssets/yellowbunnyRunBack5.png"));
                runningBackBunny.add(new Image("./assets/BunnyAssets/yellowbunnyRunBack6.png"));
            }
            
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        PhysicalState state = player.getCurrentPhysicalState();
        if (tweener % 8 == 0) {
            if (state.velX > 0) {
                lastImage = runningBunny.get(counter);
            } else if (state.velX < 0) {
                lastImage = runningBackBunny.get(counter);
            } else {
                lastImage = bunnyPlayer;
            }
            counter++;
            if (counter == runningBunny.size()) {
                counter = 0;
            }
        }
        
        g.drawImage(lastImage, state.posX + offsetX, state.posY + offsetY);
        tweener++;
        /*
         * // if we ever need to draw hitboxes again: g.pushTransform(); g.translate(offsetX, offsetY); g.setColor(Color.red);
         * g.draw(player.getShape(0)); g.setColor(Color.green); g.draw(player.getShape(0).transform(Transform.createTranslateTransform(0, 5)));
         * g.setColor(Color.white); g.resetTransform(); g.popTransform();
         */
    }
}
