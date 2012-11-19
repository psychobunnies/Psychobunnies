package com.gravity.map;

import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.Tile;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMapPlus;

public enum TileType {
    //@formatter:off
    GROUND_TOP  (MapType.GROUND, "bunnyTile", 0, 0), 
    GROUND_MID  (MapType.GROUND, "betterBunnyDirt", 0, 0),
    SPIKE       (MapType.SPIKE, "spikes", 0, 0), 
    BOUNCY      (MapType.BOUNCY, "mapTiles", 0, 0),
    
    PINK_START  (MapType.START, "markers", 0, 0),
    YELLOW_START(MapType.START, "markers", 1, 0),
    HELP_TRIGGER(MapType.TEXT, "markers", 2, 0);
    //@formatter:on

    public final MapType type;
    public final String tileSet;
    public final int tileSetX, tileSetY;

    private TileType(MapType type, String tileSet, int tileSetX, int tileSetY) {
        this.type = type;
        this.tileSet = tileSet;
        this.tileSetX = tileSetX;
        this.tileSetY = tileSetY;
    }

    /**
     * Given a map and a tile on the map, get the type of the tile.
     * 
     * @return null if the tile is not recognized.
     */
    static public TileType toTileType(TiledMapPlus map, Tile tile) {
        TileSet tileSet = map.findTileSet(tile.gid);
        Layer layer = map.getLayer(tile.layerName);
        int id = layer.getLocalTileId(tile.x, tile.y);
        int lx = tileSet.getTileX(id);
        int ly = tileSet.getTileY(id);

        for (TileType type : TileType.values()) {
            if (type.tileSet.equals(tileSet.name) && type.tileSetX == lx && type.tileSetY == ly) {
                return type;
            }
        }
        return null;
    }

    /**
     * Given a map and a tile on the map, get the type of the tile.
     * 
     * @return null if the tile is not recognized.
     */
    static public TileType toTileType(TiledMapPlus map, int x, int y, int layerIndex) {
        TileSet tileSet = map.findTileSet(map.getTileId(x, y, layerIndex));
        Layer layer = map.getLayer(layerIndex);
        int id = layer.getLocalTileId(x, y);
        int lx = tileSet.getTileX(id);
        int ly = tileSet.getTileY(id);
        for (TileType type : TileType.values()) {
            if (type.tileSet.equals(tileSet.name) && type.tileSetX == lx && type.tileSetY == ly) {
                return type;
            }
        }
        return null;
    }
}
