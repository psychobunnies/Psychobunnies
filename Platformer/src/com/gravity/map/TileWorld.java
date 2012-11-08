package com.gravity.map;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.gravity.entity.SpikeEntity;
import com.gravity.entity.TileWorldCollidable;
import com.gravity.entity.VictoryTile;
import com.gravity.geom.Rect;
import com.gravity.physics.Collidable;
import com.gravity.root.GameplayControl;

public class TileWorld implements GameWorld {
    public final int height;
    public final int width;

    public final int tileHeight;
    public final int tileWidth;

    private List<Collidable> entityNoCalls, entityCallColls;
    private List<VictoryTile> victoryTiles;

    private final String name;
    private final TiledMap map;
    private final GameplayControl controller;

    private final int TILES_LAYER_ID;
    private final int SPIKES_LAYER_ID;
    private final int PLAYERS_LAYER_ID;
    private final int VICTORY_LAYER_ID;

    private static final String TILES_LAYER_NAME = "collisions";
    private static final String SPIKES_LAYER_NAME = "spikes";
    private static final String PLAYERS_LAYER_NAME = "players";
    private static final String VICTORY_LAYER_NAME = "victory";

    private static final Vector2f PLAYER_ONE_DEFAULT_STARTPOS = new Vector2f(256, 512);
    private static final Vector2f PLAYER_TWO_DEFAULT_STARTPOS = new Vector2f(224, 512);

    private interface CollidableCreator<T> {
        T createCollidable(Rect r);
    }

    public TileWorld(String name, TiledMap map, GameplayControl controller) {
        TILES_LAYER_ID = map.getLayerIndex(TILES_LAYER_NAME);
        SPIKES_LAYER_ID = map.getLayerIndex(SPIKES_LAYER_NAME);
        PLAYERS_LAYER_ID = map.getLayerIndex(PLAYERS_LAYER_NAME);
        VICTORY_LAYER_ID = map.getLayerIndex(VICTORY_LAYER_NAME);
        this.map = map;
        this.controller = controller;
        this.name = name;

        // Get width/height
        this.tileWidth = map.getTileWidth();
        this.tileHeight = map.getTileHeight();
        this.width = map.getWidth() * tileWidth;
        this.height = map.getHeight() * tileHeight;
    }

    private <T> List<T> processLayer(int layerId, CollidableCreator<T> creator) {
        boolean[][] visited = new boolean[map.getWidth()][map.getHeight()];
        List<T> res = Lists.newArrayList();
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

        entityNoCalls = processLayer(TILES_LAYER_ID, new CollidableCreator<Collidable>() {
            @Override
            public Collidable createCollidable(Rect r) {
                return new TileWorldCollidable(r);
            }
        });

        if (VICTORY_LAYER_ID == -1) {
            victoryTiles = createGenericVictoryTiles();
        } else {
            victoryTiles = processLayer(VICTORY_LAYER_ID, new CollidableCreator<VictoryTile>() {
                @Override
                public VictoryTile createCollidable(Rect r) {
                    return new VictoryTile(r);
                }
            });
        }

        entityCallColls = processLayer(SPIKES_LAYER_ID, new CollidableCreator<Collidable>() {
            @Override
            public Collidable createCollidable(Rect r) {
                return new SpikeEntity(controller, r);
            }
        });
    }

    private List<VictoryTile> createGenericVictoryTiles() {
        System.err.println("WARNING: Map \"" + name + "\" doesn't contain victory tiles, using default victory tiles on right map edge instead.");
        return Lists.newArrayList(new VictoryTile(new Rect((map.getWidth() - 2) * tileWidth, 0, 2 * tileWidth, map.getHeight() * tileHeight)));
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

    @Override
    public List<Vector2f> getPlayerStartPositions() {
        if (PLAYERS_LAYER_ID == -1) {
            System.err.println("WARNING: Map \"" + name + "\" doesn't contain player start positions, using default positions instead.");
            return Lists.newArrayList(PLAYER_ONE_DEFAULT_STARTPOS, PLAYER_TWO_DEFAULT_STARTPOS);
        } else {
            List<Vector2f> res = Lists.newArrayList();
            for (int i = 0; i < map.getWidth(); i++) {
                for (int j = 0; j < map.getHeight(); j++) {
                    if (map.getTileId(i, j, PLAYERS_LAYER_ID) != 0) {
                        res.add(new Vector2f(i * tileWidth, j * tileHeight));
                    }
                }
            }
            Preconditions.checkArgument(res.size() == 2, "Wrong number of player start positions in map \"" + name + "\", expected 2 but found "
                    + res.size());
            return res;
        }
    }

    @Override
    public List<VictoryTile> getVictoryTiles() {
        return victoryTiles;
    }
}
