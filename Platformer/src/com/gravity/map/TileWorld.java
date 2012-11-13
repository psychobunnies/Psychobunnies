package com.gravity.map;

import java.util.List;
import java.util.Map;

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
import com.google.common.collect.Maps;
import com.gravity.entity.TriggeredText;
import com.gravity.entity.TriggeredTextEntity;
import com.gravity.geom.Rect;
import com.gravity.physics.Collidable;
import com.gravity.root.GameplayControl;

public class TileWorld implements GameWorld {
    public final int height;
    public final int width;

    public final int tileHeight;
    public final int tileWidth;

    private List<Collidable> entityNoCalls, entityCallColls;
    private List<TriggeredText> triggeredTexts;
    private Map<Layer, List<MovingCollidable>> movingCollMap;

    private final String name;
    private final TiledMapPlus map;
    private final GameplayControl controller;

    private static final String TILES_LAYER_NAME = "collisions";
    private static final String SPIKES_LAYER_NAME = "spikes";
    private static final String PLAYERS_LAYER_NAME = "players";
    private static final String MARKERS_LAYER_NAME = "level markers";
    private static final String BOUNCYS_LAYER_NAME = "bouncys";
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
        Layer terrain = map.getLayer("map");
        if (terrain != null) {

        } else {

            entityNoCalls = processLayer(TILES_LAYER_NAME, new CollidableCreator() {
                @Override
                public Collidable createCollidable(Rect r) {
                    return new StaticCollidable(r);
                }
            });

            entityCallColls = processLayer(SPIKES_LAYER_NAME, new CollidableCreator() {
                @Override
                public Collidable createCollidable(Rect r) {
                    return new SpikeEntity(controller, r);
                }
            });

            entityNoCalls.addAll(processLayer(BOUNCYS_LAYER_NAME, new CollidableCreator() {
                @Override
                public Collidable createCollidable(Rect r) {
                    return new BouncyTile(r);
                }
            }));
        }

        triggeredTexts = Lists.newArrayList();
        for (Layer layer : map.getLayers()) {
            int x = Integer.parseInt(layer.props.getProperty("x", "-1"));
            int y = Integer.parseInt(layer.props.getProperty("y", "-1"));
            String text = layer.props.getProperty("text", null);
            if (x < 0 || y < 0 || text == null) {
                continue;
            }

            // if text layer is found, make layer invisible
            layer.visible = false;
            TriggeredText triggeredText;
            triggeredText = new TriggeredText(x, y, text);
            System.out.println("found text layer: " + text);
            triggeredTexts.add(triggeredText);
            try {
                for (Tile tile : layer.getTiles()) {
                    Rect r = new Rect(tile.x * tileWidth, tile.y * tileHeight, tileWidth, tileHeight);
                    TriggeredTextEntity tte = new TriggeredTextEntity(r, triggeredText);
                    entityCallColls.add(tte);
                }
            } catch (SlickException e) {
                throw new RuntimeException("Unable to get tiles for map layer " + layer.name, e);
            }
        }

        movingCollMap = Maps.newHashMap();
        for (Layer layer : map.getLayers()) {
            final float speed = Float.parseFloat(layer.props.getProperty("speed", "-1.0"));
            final int transX = Integer.parseInt(layer.props.getProperty("translationX", "-1"));
            final int transY = Integer.parseInt(layer.props.getProperty("translationY", "-1"));
            if (speed < 0 || transX < 0 || transY < 0) continue;

            // Found a moving layer.
            layer.visible = false;
            List<Collidable> colls = processLayer(layer.name, new CollidableCreator() {
                @Override
                public Collidable createCollidable(Rect r) {
                    return new MovingCollidable(tileWidth, tileHeight, r, transX, transY, speed);
                }
            });
            entityNoCalls.addAll(colls);
            
            List<MovingCollidable> movingColls = Lists.newArrayList();
            for (Collidable c : colls) {
                movingColls.add((MovingCollidable)c);
            }
            movingCollMap.put(layer, movingColls);
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
        layer.visible = false;
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

    public List<TriggeredText> getTriggeredTexts() {
        return triggeredTexts;
    }

    public Map<Layer, List<MovingCollidable>> getMovingCollMap() {
        return movingCollMap;
    }
}
