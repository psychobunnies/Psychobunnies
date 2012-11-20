package com.gravity.map;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.Layer;

import com.google.common.collect.Lists;
import com.gravity.entity.TriggeredText;
import com.gravity.entity.TriggeredTextRenderer;
import com.gravity.map.tiles.MovingCollidableRenderer;
import com.gravity.root.Renderer;

public class TileWorldRenderer implements Renderer {
    private TileWorld tileMap;
    private Image Background1;
    private Image Background2;
    private Image Background3;
    private Image Background4;
    private Image lastBackGround;
    private List<Image> backgrounds;
    int tweener;
    private List<Renderer> extraRenderers;

    public TileWorldRenderer(TileWorld tileMap) {
        this.tileMap = tileMap;
        try {
            // "./assets/BambooForest2small.png"
            // "./assets/2ufa5h5.png"
            Background1 = new Image("./assets/FrogAssets/frogtower.png");
            Background2 = new Image("./assets/FrogAssets/frogtower1.png");
            Background3 = new Image("./assets/FrogAssets/frogtower2.png");
            Background4 = new Image("./assets/FrogAssets/frogtower3.png");
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
        backgrounds = Lists.newArrayList();
        backgrounds.add(Background1);
        backgrounds.add(Background2);
        backgrounds.add(Background3);
        backgrounds.add(Background4);
        extraRenderers = Lists.newArrayList();
        for (TriggeredText tt : tileMap.getTriggeredTexts()) {
            extraRenderers.add(new TriggeredTextRenderer(tt));
        }
        for (Layer l : tileMap.getMovingCollMap().keySet()) {
            extraRenderers.add(new MovingCollidableRenderer(tileMap, l));
        }
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        if (tweener % 15 == 0) {
            if (lastBackGround == null) {
                g.drawImage(Background1, 0, 0);
                lastBackGround = Background1;
            } else {
                g.drawImage((backgrounds.get(backgrounds.indexOf(lastBackGround) + 1)), 0, 0);
                lastBackGround = backgrounds.get(backgrounds.indexOf(lastBackGround) + 1);
                if (lastBackGround == Background4) {
                    lastBackGround = Background1;
                }
            }
        } else {
            g.drawImage(lastBackGround, 0, 0);
        }
        tweener++;

        // TiledMap supports easy rendering. Let's use it!
        // Later we'll need to some how adjust x,y for offset/scrolling
        tileMap.render(g, offsetX, offsetY);

        for (Renderer renderer : extraRenderers) {
            renderer.render(g, offsetX, offsetY);
        }
    }
}
