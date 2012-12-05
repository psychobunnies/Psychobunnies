package com.gravity.map;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
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
    TABLE_LEG   (MapType.GROUND, "lab-sheet", 0, 0),
    TABLE_FOOT  (MapType.GROUND, "lab-sheet", 0, 1),
    TABLE_SINGLE(MapType.GROUND, "lab-sheet", 0, 2),
    TABLE_LEFT  (MapType.GROUND, "lab-sheet", 1, 0),
    TABLE_MID   (MapType.GROUND, "lab-sheet", 2, 0),
    TABLE_RIGHT (MapType.GROUND, "lab-sheet", 3, 0),
    TABLE_LSHEEN(MapType.GROUND, "lab-sheet", 1, 1),
    TABLE_MSHEEN(MapType.GROUND, "lab-sheet", 2, 1),
    TABLE_RSHEEN(MapType.GROUND, "lab-sheet", 3, 1),
    TABLE_LBLOOD(MapType.GROUND, "lab-sheet", 1, 2),
    TABLE_MBLOOD(MapType.GROUND, "lab-sheet", 2, 2),
    TABLE_RBLOOD(MapType.GROUND, "lab-sheet", 3, 2),
    
    GROUND_TOP  (MapType.GROUND, "bunnyTiles", 0, 0), 
    GROUND_MID  (MapType.GROUND, "bunnyTiles", 1, 0),
    
    SPIKE       (MapType.SPIKE, "spikes", 0, 0), 
    BOUNCY      (MapType.BOUNCY, "mapTiles", 0, 0),
    
    PLAYER_KEYED_WARNING(MapType.PLAYER_KEYED, "levelMarkers", 1, 2),
    PLAYER_KEYED_UNSET(MapType.GROUND, "bunnyTiles", 1, 1),
    PLAYER_KEYED_PINK(MapType.PLAYER_KEYED, "levelMarkers", 1, 0),
    PLAYER_KEYED_YELLOW(MapType.PLAYER_KEYED, "levelMarkers", 1, 1),
    
    PINK_START  (MapType.START, "levelMarkers", 0, 0),
    YELLOW_START(MapType.START, "levelMarkers", 1, 0),
    HELP_TRIGGER(MapType.TEXT,  "levelMarkers", 2, 0),
    
    LEVEL_CAGE  (MapType.LEVEL, "levelMarkers", 2, 1),
    
    UNKNOWN (MapType.UNKNOWN, "", 0, 0);
    //@formatter:on

    public static Image UNKNOWN_IMAGE = null;
    static {
        try {
            UNKNOWN_IMAGE = new Image(1, 1);
        } catch (SlickException e) {
            throw new RuntimeException();
        }
    }

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
        return UNKNOWN;
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
        return UNKNOWN;
    }

    public Image getImage(TiledMapPlus map) {
        if (this == UNKNOWN) {
            return UNKNOWN_IMAGE;
        }
        Integer id = map.getTilesetID(this.tileSet);
        if (id == null) {
            throw new RuntimeException("Could not find tileset for " + this);
        }
        TileSet tileSet = map.getTileSet(id);
        if (tileSet == null) {
            throw new RuntimeException("Could not get tileset for " + this);
        }
        return tileSet.tiles.getSubImage(tileSetX, tileSetY);
    }

    static public Image getImage(TiledMapPlus map, Tile tile) {
        TileSet tileSet = map.findTileSet(tile.gid);
        Layer layer = map.getLayer(tile.layerName);
        int id = layer.getLocalTileId(tile.x, tile.y);
        int lx = tileSet.getTileX(id);
        int ly = tileSet.getTileY(id);

        return tileSet.tiles.getSubImage(lx, ly);
    }

    static public Image getImage(TiledMapPlus map, int x, int y, int layerIndex) {
        TileSet tileSet = map.findTileSet(map.getTileId(x, y, layerIndex));
        Layer layer = map.getLayer(layerIndex);
        int id = layer.getLocalTileId(x, y);
        int lx = tileSet.getTileX(id);
        int ly = tileSet.getTileY(id);

        return tileSet.tiles.getSubImage(lx, ly);
    }
}
