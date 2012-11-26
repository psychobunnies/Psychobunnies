package com.gravity.map;

/**
 * Enum containing types of tiles that can be found in our levels. Useful if some set of tile pictures map to the same tile mechanic. Use in
 * conjunction with {@link TileType}.
 * 
 * @author xiao
 */
public enum MapType {
    //@formatter:off
    GROUND, 
    SPIKE, 
    BOUNCY,
    START,
    FINISH,
    TEXT,
    PLAYER_KEYED,
    LEVEL, 
    UNKNOWN
    //@formatter:on
}
