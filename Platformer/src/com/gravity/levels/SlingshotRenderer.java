package com.gravity.levels;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.gravity.fauna.Player;
import com.gravity.geom.Rect;

public class SlingshotRenderer implements Renderer {
    
    private Player selfPlayer, otherPlayer;
    
    private static Image blueHandRight, blueHandLeft, yellowHandRight, yellowHandLeft;
    private Image handRight, handLeft;
    private Color color;
    
    static {
        try {
            blueHandRight = new Image("./assets/HandAssets/HandRight.png");
            blueHandLeft = new Image("./assets/HandAssets/HandLeft.png");
            yellowHandRight = new Image("./assets/HandAssets/HandRightYellow.png");
            yellowHandLeft = new Image("./assets/HandAssets/HandLeftYellow.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
    
    public SlingshotRenderer(Player selfPlayer, Player otherPlayer) {
        this.selfPlayer = selfPlayer;
        this.otherPlayer = otherPlayer;
        
        if (selfPlayer.getName().equals("pink")) {
            handRight = blueHandRight;
            handLeft = blueHandLeft;
            color = new Color(26, 106, 255);
        } else {
            handRight = yellowHandRight;
            handLeft = yellowHandLeft;
            color = new Color(255, 246, 0);
        }
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        g.pushTransform();
        g.translate(offsetX, offsetY);
        if (selfPlayer.slingshot) {
            Rect selfRect = selfPlayer.getPhysicalState().getRectangle();
            Rect otherRect = otherPlayer.getPhysicalState().getRectangle();
            
            g.setColor(color);
            g.setLineWidth(selfPlayer.slingshotStrength * 10);
            
            if (selfRect.getX() < otherRect.getX()) {
                g.drawLine(selfRect.getCenter().x, selfRect.getCenter().y + 15,
                           otherRect.getCenter().x - 8, otherRect.getCenter().y + 15);
                g.drawImage(handRight, otherRect.getCenter().x - 15, otherRect.getCenter().y);
            } else {
                g.drawLine(selfRect.getCenter().x, selfRect.getCenter().y + 15,
                           otherRect.getCenter().x + 10, otherRect.getCenter().y + 15);
                g.drawImage(handLeft, otherRect.getCenter().x - 15, otherRect.getCenter().y);
            }
        }
        g.popTransform();
    }

}
