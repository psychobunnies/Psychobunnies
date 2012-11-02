package com.gravity.map;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.tiled.TiledMap;

import com.google.common.collect.Lists;
import com.gravity.entity.SpikeEntity;
import com.gravity.entity.TileWorldEntity;
import com.gravity.geom.Rect;
import com.gravity.physics.Collidable;
import com.gravity.root.GameplayControl;

public class TileWorld implements GameWorld {
    public final int height;
    public final int width;

    public final int tileHeight;
    public final int tileWidth;

    private List<Collidable> entityNoCalls, entityCallColls;

    private final TiledMap map;
    private final GameplayControl controller;

    private final int TILES_LAYER_ID;
    private final int SPIKES_LAYER_ID;

    private interface CollidableCreator {

        Collidable createCollidable(Rect r);

    }

    public TileWorld(TiledMap map, GameplayControl controller) {
        TILES_LAYER_ID = map.getLayerIndex("collisions");
        SPIKES_LAYER_ID = map.getLayerIndex("spikes");
        this.map = map;
        this.controller = controller;

        // Get width/height
        this.tileWidth = map.getTileWidth();
        this.tileHeight = map.getTileHeight();
        this.width = map.getWidth() * tileWidth;
        this.height = map.getHeight() * tileHeight;
    }

    private List<Collidable> processLayer(int layerId, CollidableCreator creator) {
        boolean[][] visited = new boolean[map.getWidth()][map.getHeight()];
        List<Collidable> res = Lists.newArrayList();
        int first, i, j, tileId;
        for (i = 0; i < map.getWidth(); i++) {
            first = 0;
            while (first < map.getHeight()) {
                tileId = map.getTileId(i, first, layerId);
                visited[i][first] = true;
                if (tileId != 0) {
                    j = first + 1;
                    while (j < map.getHeight() && map.getTileId(i, j, layerId) != 0) {
                        visited[i][j] = true;
                        j++;
                    }
                    Rect r = new Rect(i * tileWidth, first * tileHeight, tileWidth, tileHeight * (j - first));
                    res.add(creator.createCollidable(r));
                    first = j;
                } else {
                    first++;
                }
            }
        }

        for (j = 0; j < map.getHeight(); j++) {
            first = 0;
            while (first < map.getWidth()) {
                tileId = visited[first][j] ? 0 : map.getTileId(first, j, layerId);
                if (tileId != 0) {
                    i = first + 1;
                    while (i < map.getWidth() && map.getTileId(i, j, layerId) != 0) {
                        visited[i][j] = true;
                        i++;
                    }
                    Rect r = new Rect(first * tileWidth, j * tileHeight, tileWidth * (i - first), tileHeight);
                    res.add(creator.createCollidable(r));
                    first = i;
                } else {
                    first++;
                }
            }
        }
        return res;
    }

    @Override
    public void initialize() {
        // Iterate over and find all tiles

        entityNoCalls = processLayer(TILES_LAYER_ID, new CollidableCreator() {
            @Override
            public Collidable createCollidable(Rect r) {
                return new TileWorldEntity(r);
            }
        });

        entityCallColls = processLayer(SPIKES_LAYER_ID, new CollidableCreator() {
            @Override
            public Collidable createCollidable(Rect r) {
                return new SpikeEntity(controller, r);
            }
        });
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
}
