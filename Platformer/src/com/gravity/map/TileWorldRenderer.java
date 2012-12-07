package com.gravity.map;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.google.common.collect.Lists;
import com.gravity.entity.TriggeredImage;
import com.gravity.entity.TriggeredText;
import com.gravity.entity.TriggeredTextRenderer;
import com.gravity.levels.Renderer;
import com.gravity.map.tiles.MovingEntity;
import com.gravity.root.PlatformerGame;

public class TileWorldRenderer implements Renderer {
    private TileWorld tileMap;
    private Image background;
    private List<Renderer> extraRenderers;

    public TileWorldRenderer(TileWorld tileMap) {
        this.tileMap = tileMap;
        try {
            background = new Image("assets/background-no-shelf.png");
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
        extraRenderers = Lists.newArrayList();
        for (TriggeredText tt : tileMap.getTriggeredTexts()) {
            extraRenderers.add(new TriggeredTextRenderer(tt));
        }
        for (TriggeredImage ti : tileMap.getTriggeredImages()) {
            extraRenderers.add(ti);
        }
        for (List<MovingEntity> mcs : tileMap.getMovingCollMap().values()) {
            extraRenderers.addAll(mcs);
        }
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        g.drawImage(background, 0, 0, PlatformerGame.WIDTH, PlatformerGame.HEIGHT, 0, 0, PlatformerGame.WIDTH, PlatformerGame.HEIGHT);

        // TiledMap supports easy rendering. Let's use it!
        // Later we'll need to some how adjust x,y for offset/scrolling
        tileMap.render(g, offsetX, offsetY);

        for (Renderer renderer : extraRenderers) {
            renderer.render(g, offsetX, offsetY);
        }
    }
}
