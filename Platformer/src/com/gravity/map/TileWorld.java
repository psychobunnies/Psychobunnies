package com.gravity.map;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.tiled.TiledMap;

import com.google.common.collect.Lists;
import com.gravity.entity.SpikeEntity;
import com.gravity.entity.TileWorldEntity;
import com.gravity.geom.Rect;
import com.gravity.entity.TriggeredTextEntity;
import com.gravity.entity.TriggeredText;
import com.gravity.physics.Collidable;
import com.gravity.root.GameplayControl;

public class TileWorld implements GameWorld {
    public final int height;
    public final int width;

    public final int tileHeight;
    public final int tileWidth;

    private List<Collidable> entityNoCalls, entityCallColls;
    private List<TriggeredText> triggeredTexts;

    private TiledMap map;

    public TileWorld(TiledMap map, GameplayControl controller) {
        // Get width/height
        this.tileWidth = map.getTileWidth();
        this.tileHeight = map.getTileHeight();
        this.width = map.getWidth() * tileWidth;
        this.height = map.getHeight() * tileHeight;

        this.map = map;

        entityNoCalls = Lists.newArrayList();
        entityCallColls = Lists.newArrayList();
        triggeredTexts = Lists.newArrayList();
        for (int layerId = 0; layerId < map.getLayerCount(); layerId++) {
            int x = Integer.parseInt(map.getLayerProperty(layerId, "x", "-1"));
            int y = Integer.parseInt(map.getLayerProperty(layerId, "y", "-1"));
            String text = map.getLayerProperty(layerId, "text", null);
            TriggeredText triggeredText = null;
            if (x > 0 && y > 0 && text != null) {
                triggeredText = new TriggeredText(x, y, text);
                System.out.println("found text layer: " + text);
                triggeredTexts.add(triggeredText);
            }
            
            // Iterate over and find all tiles
            for (int i = 0; i < map.getWidth(); i++) {
                for (int j = 0; j < map.getHeight(); j++) {
                    int tileId = map.getTileId(i, j, layerId);
                    if (tileId != 0) {
                        // Tile exists at this spot
                        Rect r = new Rect(i * tileWidth, j * tileHeight, tileWidth, tileHeight);
                        if (layerId == map.getLayerIndex("collisions")) {
                            entityNoCalls.add(new TileWorldEntity(r));
                        } else if (layerId == map.getLayerIndex("spikes")) {
                            entityCallColls.add(new SpikeEntity(controller, r));
                        } else if (triggeredText != null) { // Triggered text layer.
                            TriggeredTextEntity tte = new TriggeredTextEntity(r, triggeredText);
                            entityCallColls.add(tte);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public List<Collidable> getTerrainEntitiesNoCalls() {
        return entityNoCalls;
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        /*
         */// if we need to draw hitboxes again:

        g.pushTransform();
        g.translate(offsetX, offsetY);
        g.setColor(Color.red);
        for (Collidable e : entityNoCalls) {
            g.draw(e.getRect(0).toShape());
        }
        g.setColor(Color.white);
        g.resetTransform();
        g.popTransform();

        map.render(offsetX, offsetY);
    }

    @Override
    public List<Collidable> getTerrainEntitiesCallColls() {
        return entityCallColls;
    }

    public List<TriggeredText> getTriggeredTexts() {
        return triggeredTexts;
    }
}
