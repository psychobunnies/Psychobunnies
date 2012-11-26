package com.gravity.map;

import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.Tile;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMapPlus;

/**
 * Convenience class which provides a good place to put hardcoded constants between Tiled's system and our level format.
 * 
 * To add a new tile type, specify the tileset which the tile can be found in, and the x and y position of the tile in the tileset. Also specify the
 * type of tile it is.
 * 
 * To use, call the static method toTileType with a Tile or a layer and x,y within the layer. You'll get the tile type back.
 * 
 * @author xiao
 * 
 */
public enum TileType {
    //@formatter:off
    GROUND_TOP  (MapType.GROUND, "bunnyTiles", 0, 0), 
    GROUND_MID  (MapType.GROUND, "bunnyTiles", 1, 0),
    SPIKE       (MapType.SPIKE, "spikes", 0, 0), 
    BOUNCY      (MapType.BOUNCY, "mapTiles", 0, 0),
    
    PLAYER_KEYED_WARNING(MapType.PLAYER_KEYED, "levelMarkers", 1, 2),
    PLAYER_KEYED_UNSET(MapType.GROUND, "bunnyTiles", 1, 1),
    PLAYER_KEYED_PINK(MapType.PLAYER_KEYED, "levelMarkers", 1, 0),
    PLAYER_KEYED_YELLOW(MapType.PLAYER_KEYED, "levelMarkers", 1, 1),
    
    PINK_START  (MapType.START, "levelMarkers", 0, 0),
    YELLOW_START(MapType.START, "levelMarkers", 0, 1),
    HELP_TRIGGER(MapType.TEXT, "levelMarkers", 2, 0);
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
