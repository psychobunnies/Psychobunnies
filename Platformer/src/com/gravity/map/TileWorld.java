package com.gravity.map;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.GroupObject;
import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.ObjectGroup;
import org.newdawn.slick.tiled.Tile;
import org.newdawn.slick.tiled.TiledMapPlus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.gravity.geom.Rect;
import com.gravity.physics.Collidable;
import com.gravity.root.GameplayControl;

public class TileWorld implements GameWorld {
    public final int height;
    public final int width;

    public final int tileHeight;
    public final int tileWidth;

    private List<Collidable> entityNoCalls, entityCallColls;

    private final String name;
    private final TiledMapPlus map;
    private final GameplayControl controller;

    private static final String TILES_LAYER_NAME = "collisions";
    private static final String SPIKES_LAYER_NAME = "spikes";
    private static final String PLAYERS_LAYER_NAME = "players";
    private static final String MARKERS_LAYER_NAME = "level markers";
    private static final String FINISH_MARKER_NAME = "finish";

    private static final Vector2f PLAYER_ONE_DEFAULT_STARTPOS = new Vector2f(256, 512);
    private static final Vector2f PLAYER_TWO_DEFAULT_STARTPOS = new Vector2f(224, 512);

    private interface CollidableCreator {
        Collidable createCollidable(Rect r);
    }

    public TileWorld(String name, TiledMapPlus map, GameplayControl controller) {
        this.map = map;
        this.controller = controller;
        this.name = name;

        // Get width/height
        this.tileWidth = map.getTileWidth();
        this.tileHeight = map.getTileHeight();
        this.width = map.getWidth() * tileWidth;
        this.height = map.getHeight() * tileHeight;
    }

    /**
     * Process a layer of the map for collisions. Merge adjacent tiles vertically, then horizontally.
     * 
     * @param layerName
     *            the name of the layer in the map to process.
     * @param creator
     *            a Creator to create collidables for
     * @return a list of collidables in this layer. Returns an empty list if the list does not exist.
     */
    private List<Collidable> processLayer(String layerName, CollidableCreator creator) {
        boolean[][] visited = new boolean[map.getWidth()][map.getHeight()];
        List<Collidable> res = Lists.newArrayList();
        int layer = 0;
        try {
            layer = map.getLayerID(layerName);
        } catch (NullPointerException e) {
            System.err.println("WARNING: Layer " + layerName + " not found, returning empty collidables list.");
            return res;
        }

        int first, i, j, tileId;
        for (i = 0; i < map.getWidth(); i++) {
            first = 0;
            while (first < map.getHeight()) {
                tileId = map.getTileId(i, first, layer);
                visited[i][first] = true;
                if (tileId != 0) {
                    j = first + 1;
                    while (j < map.getHeight() && map.getTileId(i, j, layer) != 0) {
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
                tileId = visited[first][j] ? 0 : map.getTileId(first, j, layer);
                if (tileId != 0) {
                    i = first + 1;
                    while (i < map.getWidth() && map.getTileId(i, j, layer) != 0) {
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

        entityNoCalls = processLayer(TILES_LAYER_NAME, new CollidableCreator() {
            @Override
            public Collidable createCollidable(Rect r) {
                return new TileWorldCollidable(r);
            }
        });

        entityCallColls = processLayer(SPIKES_LAYER_NAME, new CollidableCreator() {
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
        //@formatter:off
        /*
         * // if we need to draw hitboxes again:

        g.pushTransform();
        g.translate(offsetX, offsetY);
        g.setColor(Color.red);
        for (Collidable e : entityNoCalls) {
            g.draw(e.getRect(0).toShape());
        }
        g.setColor(Color.white);
        g.resetTransform();
        g.popTransform();
        */ 
        //@formatter:on

        map.render(offsetX, offsetY);
    }

    @Override
    public List<Collidable> getTerrainEntitiesCallColls() {
        return entityCallColls;
    }

    @Override
    public List<Vector2f> getPlayerStartPositions() {
        Layer layer = map.getLayer(PLAYERS_LAYER_NAME);
        if (layer == null) {
            System.err.println("WARNING: Map \"" + name + "\" doesn't contain player start positions, using default positions instead.");
            return Lists.newArrayList(PLAYER_ONE_DEFAULT_STARTPOS, PLAYER_TWO_DEFAULT_STARTPOS);
        }
        try {
            List<Vector2f> res = Lists.newArrayList();
            for (Tile tile : layer.getTiles()) {
                res.add(new Vector2f(tile.x * tileWidth, tile.y * tileHeight));
            }
            Preconditions.checkArgument(res.size() == 2, "Wrong number of player start positions in map \"" + name + "\", expected 2 but found "
                    + res.size());
            return res;
        } catch (SlickException e) {
            System.err.println(e);
            return Lists.newArrayList(PLAYER_ONE_DEFAULT_STARTPOS, PLAYER_TWO_DEFAULT_STARTPOS);
        }
    }

    @Override
    public Rect getFinishRect() {
        GroupObject object;
        try {
            ObjectGroup group = map.getObjectGroup(MARKERS_LAYER_NAME);
            object = group.getObject(FINISH_MARKER_NAME);
            return new Rect(object.x, object.y, object.width, object.height);
        } catch (NullPointerException e) {
            System.err.println("No marker layer found for map " + map + " using right edge of map instead");
            return new Rect((map.getWidth() - 1) * tileWidth, 0, tileWidth, getHeight());
        }
    }

    @Override
    public String toString() {
        return "TileWorld [height=" + height + ", width=" + width + ", name=" + name + ", map=" + map + "]";
    }
}
