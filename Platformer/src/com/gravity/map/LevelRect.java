package com.gravity.map;

public enum LevelRect {
    FINISH(MapType.FINISH, "level markers", "finish");

    public final MapType type;
    public final String layer;
    public final String objectType;

    private LevelRect(MapType type, String layer, String objectType) {
        this.type = type;
        this.layer = layer;
        this.objectType = objectType;
    }
}
