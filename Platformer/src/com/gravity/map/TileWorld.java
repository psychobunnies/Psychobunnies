package com.gravity.map;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.GroupObject;
import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.ObjectGroup;
import org.newdawn.slick.tiled.Tile;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMapPlus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.gravity.entity.TriggeredText;
import com.gravity.entity.TriggeredTextCollidable;
import com.gravity.geom.Rect;
import com.gravity.levels.GameplayControl;
import com.gravity.map.tiles.BouncyTile;
import com.gravity.map.tiles.DisappearingTile;
import com.gravity.map.tiles.DisappearingTileController;
import com.gravity.map.tiles.MovingEntity;
import com.gravity.map.tiles.SpikeEntity;
import com.gravity.map.tiles.TileRendererDelegate;
import com.gravity.physics.Collidable;
import com.gravity.physics.CollisionEngine;

public class TileWorld implements GameWorld {

    public static final String TILES_LAYER_NAME = "collisions";
    public static final String SPIKES_LAYER_NAME = "spikes";
    public static final String PLAYERS_LAYER_NAME = "players";
    public static final String MARKERS_LAYER_NAME = "level markers";
    public static final String BOUNCYS_LAYER_NAME = "bouncys";
    public static final String FINISH_MARKER_NAME = "finish";
    public static final String DISAPPEARING_LAYER_TYPE = "disappearing";
    public static final String PLAYERKEYED_LAYER_NAME = "playerkeyed";
    public static final String FALLING_SPIKE_LAYER_NAME = "falling spikes";
    public static final String STOMPS_LAYER_NAME = "stomps";

    public static final String SPECIAL_LEVELS_LAYER_NAME = "special levels";
    public static final String QUIT_OBJECT_NAME = "quit";
    public static final String OPTIONS_OBJECT_NAME = "options";

    public static final float STOMP_SPEED_FORWARD = 50.0f;
    public static final float STOMP_SPEED_BACKWARD = 30.0f;

    public final int height;
    public final int width;

    public final int tileHeight;
    public final int tileWidth;

    private List<Collidable> entityNoCalls, entityCallColls;
    private List<TriggeredText> triggeredTexts;
    private Map<Layer, List<MovingEntity>> movingCollMap;

    private List<Vector2f> startPositions = null;
    private List<DisappearingTileController> disappearingTileControllers;

    public final String name;
    public final TiledMapPlus map;
    private final GameplayControl controller;

    private interface CollidableCreator<T extends Collidable> {
        T createCollidable(Rect r);
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
    public <T extends Collidable> List<T> processLayer(String layerName, CollidableCreator<T> creator) {
        boolean[][] visited = new boolean[map.getWidth()][map.getHeight()];
        List<T> res = Lists.newArrayList();
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
                    if (j == first + 1) {
                        visited[i][first] = false;
                    } else {
                        Rect r = new Rect(i * tileWidth, first * tileHeight, tileWidth, tileHeight * (j - first));
                        res.add(creator.createCollidable(r));
                    }
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

    public <T extends Collidable> List<T> processLayerSingleSquares(String layerName, CollidableCreator<T> creator) {
        List<T> res = Lists.newArrayList();
        int layer = 0;
        try {
            layer = map.getLayerID(layerName);
        } catch (NullPointerException e) {
            System.err.println("WARNING: Layer " + layerName + " not found, returning empty collidables list.");
            return res;
        }

        int i, j, tileId;
        for (i = 0; i < map.getWidth(); i++) {
            for (j = 0; j < map.getHeight(); j++) {
                tileId = map.getTileId(i, j, layer);
                if (tileId != 0) {
                    Rect r = new Rect(i * tileWidth, j * tileHeight, tileWidth, tileHeight);
                    res.add(creator.createCollidable(r));
                }
            }
        }
        return res;
    }

    @Override
    public void initialize() {
        // Iterate over and find all tiles
        entityNoCalls = processLayer(TILES_LAYER_NAME, new CollidableCreator<Collidable>() {
            @Override
            public Collidable createCollidable(Rect r) {
                return new StaticCollidable(r);
            }
        });

        entityCallColls = processLayer(SPIKES_LAYER_NAME, new CollidableCreator<Collidable>() {
            @Override
            public Collidable createCollidable(Rect r) {
                return new SpikeEntity(controller, r);
            }
        });

        entityNoCalls.addAll(processLayer(BOUNCYS_LAYER_NAME, new CollidableCreator<Collidable>() {
            @Override
            public Collidable createCollidable(Rect r) {
                return new BouncyTile(r);
            }
        }));

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
                    TriggeredTextCollidable tte = new TriggeredTextCollidable(r, triggeredText);
                    entityCallColls.add(tte);
                }
            } catch (SlickException e) {
                throw new RuntimeException("Unable to get tiles for map layer " + layer.name, e);
            }
        }

        movingCollMap = Maps.newHashMap();
        for (final Layer layer : map.getLayers()) {
            final float speed = Float.parseFloat(layer.props.getProperty("speed", "-1.0"));
            final int transX = Integer.parseInt(layer.props.getProperty("translationX", "-22222"));
            final int transY = Integer.parseInt(layer.props.getProperty("translationY", "-22222"));

            if (speed < 0 || transX == -22222 || transY == -22222)
                continue;

            // Found a moving layer.
            layer.visible = false;
            List<MovingEntity> colls = processLayerSingleSquares(layer.name, new CollidableCreator<MovingEntity>() {
                @Override
                public MovingEntity createCollidable(Rect r) {
                    TileType tileType = TileType.toTileType(map, Math.round(r.getX()) / tileWidth, Math.round(r.getY()) / tileHeight, layer.index);
                    TileRendererDelegate renderer = new TileRendererDelegate(map, tileType);
                    return new MovingEntity(renderer, r, transX * tileWidth, transY * tileHeight, speed);
                }
            });
            entityNoCalls.addAll(colls);

            System.out.println("found moving tiles layer " + layer.name);

            List<MovingEntity> movingColls = Lists.newArrayList();
            for (MovingEntity c : colls) {
                movingColls.add(c);
                System.out.println("--> " + c.toString());
            }

            movingCollMap.put(layer, movingColls);
        }

        Layer stomps = map.getLayer(STOMPS_LAYER_NAME);
        if (stomps != null) {
            stomps.visible = false;
            try {
                for (Tile tile : stomps.getTiles()) {
                    Rect r = new Rect(tile.x * tileWidth, tile.y * tileWidth, tileWidth, tileHeight);
                    float minBelowY = 22222222f;
                    for (Collidable coll : entityNoCalls) {
                        Rect c = coll.getPhysicalState().getRectangle();
                        if (r.getMaxX() > c.getX() && r.getX() < c.getMaxX() && c.getY() > r.getMaxY() && c.getY() < minBelowY) {
                            minBelowY = c.getY();
                        }
                    }

                    TileType tileType = TileType.toTileType(map, tile);
                    TileRendererDelegate renderer = new TileRendererDelegate(map, tileType);
                    MovingEntity stompColl = new MovingEntity(renderer, r, 0, (Math.round(minBelowY - r.getMaxY())), STOMP_SPEED_FORWARD,
                            STOMP_SPEED_BACKWARD);

                    movingCollMap.put(stomps, Lists.newArrayList(stompColl));
                    entityNoCalls.add(stompColl);
                }
            } catch (SlickException e) {
                throw new RuntimeException("Unable to get tiles for stomps", e);
            }
        }

        for (Layer layer : map.getLayers()) {
            String type = layer.props.getProperty("type", "");
            if (!type.equals("checkpoint"))
                continue;

            layer.visible = false;
            try {
                Vector2f startPosA = null, startPosB = null;
                for (Tile tile : layer.getTiles()) {
                    int tileID = layer.getTileID(tile.x, tile.y);
                    TileSet tileSet = map.getTileSetByGID(tileID);
                    int startIDA = tileSet.getGlobalIDByPosition(3, 1);
                    int startIDB = tileSet.getGlobalIDByPosition(3, 2);
                    if (tileID == startIDA) {
                        // Pink start
                        startPosA = new Vector2f(tile.x * tileWidth, tile.y * tileHeight);
                    } else if (tileID == startIDB) { // !!!!
                        // Yellow start
                        startPosB = new Vector2f(tile.x * tileWidth, tile.y * tileHeight);
                    }
                }
                if (startPosA == null || startPosB == null) {
                    System.err.println("WARNING: skipping checkpoint layer " + layer.name);
                    continue;
                }

                List<Vector2f> newStartPositions = Lists.newArrayList(startPosA, startPosB);
                Checkpoint checkpoint = new Checkpoint(controller, newStartPositions);
                for (Tile tile : layer.getTiles()) {
                    int tileID = layer.getTileID(tile.x, tile.y);
                    TileSet tileSet = map.getTileSetByGID(tileID);
                    int checkID = tileSet.getGlobalIDByPosition(3, 0);
                    if (layer.getTileID(tile.x, tile.y) == checkID) {
                        Rect r = new Rect(tile.x * tileWidth, tile.y * tileHeight, tileWidth, tileHeight);
                        entityCallColls.add(new CheckpointCollidable(checkpoint, r));
                    }
                }
            } catch (SlickException e) {
                e.printStackTrace();
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
    public Layer getLayer(String name) {
        return map.getLayer(name);
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
        if (startPositions != null) {
            return startPositions;
        }

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

    /** Returns a list of DisappearingTileController instances if any disappearing tile layers are found. */
    @Override
    public List<DisappearingTileController> reinitializeDisappearingLayers(final CollisionEngine engine) {
        disappearingTileControllers = Lists.newArrayList();
        for (Layer l : map.getLayers()) {
            if (l.props.getProperty("type", "").equals(DISAPPEARING_LAYER_TYPE)) {
                final DisappearingTileController controller = createDisappearingTileController(l);
                List<DisappearingTile> coll = processLayer(l.name, new CollidableCreator<DisappearingTile>() {
                    @Override
                    public DisappearingTile createCollidable(Rect r) {
                        return new DisappearingTile(r, controller, engine);
                    }
                });
                entityNoCalls.addAll(coll);
                disappearingTileControllers.add(controller);
                for (DisappearingTile c : coll) {
                    controller.register(c);
                }
            }
        }
        return disappearingTileControllers;
    }

    private DisappearingTileController createDisappearingTileController(Layer l) {
        float invisibleTime = Float.parseFloat(l.props.getProperty(INVISIBLE_TIME_PROPERTY));
        float normalVisibleTime = Float.parseFloat(l.props.getProperty(NORMAL_VISIBLE_TIME_PROPERTY));
        float flickerTime = Float.parseFloat(l.props.getProperty(FLICKER_TIME_PROPERTY));
        float geometricParameter = Float.parseFloat(l.props.getProperty(GEOMETRIC_PARAMETER_PROPERTY));
        int flickerCount = Integer.parseInt(l.props.getProperty(FLICKER_COUNT_PROPERTY));

        return new DisappearingTileController(invisibleTime, normalVisibleTime, flickerTime, geometricParameter, flickerCount, l);
    }

    public Map<Layer, List<MovingEntity>> getMovingCollMap() {
        return movingCollMap;
    }

    public void setStartPositions(List<Vector2f> startPositions) {
        this.startPositions = startPositions;
    }

    /**
     * Returns the bottom center of all level cage locations.
     */
    public SortedSet<Vector2f> getLevelLocations() {
        Layer layer;
        if ((layer = map.getLayer(MARKERS_LAYER_NAME)) != null) {
            layer.visible = false;
            SortedSet<Vector2f> locs = Sets.newTreeSet();
            try {
                for (Tile tile : layer.getTiles()) {
                    locs.add(new Vector2f(tile.x * tileWidth, (tile.y + 1) * tileHeight));
                }
                return locs;
            } catch (SlickException e) {
                throw new RuntimeException("Error while trying to get level locations from layer", e);
            }
        }
        System.err.println("WARNING: could not get level cage locations: level markers layer does not exist");
        return Sets.newTreeSet();
    }

    /**
     * Return the location of a cage in the special levels layer.
     * 
     * @param name Name of the object to find.
     * @return null if the location cannot be found.
     */
    public Vector2f getSpecialLocation(String name) {
        ObjectGroup group;
        if ((group = map.getObjectGroup(SPECIAL_LEVELS_LAYER_NAME)) != null) {
            GroupObject object;
            if ((object = group.getObject(name)) != null) {
                return new Vector2f(object.x, object.y + tileHeight);
            }
            System.err.println("WARNING: could not find " + name + " location: " + name + " object does not exit on special levels layer");
        }
        System.err.println("WARNING: could not find " + name + " location: special levels layer does not exist");
        return null;
    }
    
    /**
     * Return the location of the quit cage.
     * 
     * @return null if the location cannot be found.
     */
    public Vector2f getQuitLocation() {
        return getSpecialLocation(QUIT_OBJECT_NAME);
    }

    /**
     * Return the location of the options cage.
     * 
     * @return null if the location cannot be found.
     */
    public Vector2f getOptionsLocation() {
        return getSpecialLocation(OPTIONS_OBJECT_NAME);
    }
}
