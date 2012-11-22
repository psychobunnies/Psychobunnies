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
        for (Layer l : tileMap.getMovingCollMap().keySet()) {
            extraRenderers.add(new MovingCollidableRenderer(tileMap, l));
        }
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        g.drawImage(background, 0, 0);

        // TiledMap supports easy rendering. Let's use it!
        // Later we'll need to some how adjust x,y for offset/scrolling
        tileMap.render(g, offsetX, offsetY);

        for (Renderer renderer : extraRenderers) {
            renderer.render(g, offsetX, offsetY);
        }
    }
}
