package com.gravity.root;

public class LevelInfo {
    static private final int STATE_ID_OFFSET = 1000;

    public final String title;
    public final String description;
    public final String mapfile;
    public final int stateId;
    public final int levelOrder;

    public LevelInfo(String title, String description, String mapfile, int order) {
        this.title = title;
        this.description = description;
        this.mapfile = mapfile;
        this.stateId = order + STATE_ID_OFFSET;
        this.levelOrder = order;
    }

    @Override
    public String toString() {
        return "LevelInfo [title=" + title + ", description=" + description + ", mapfile=" + mapfile + ", stateId=" + stateId + "]";
    }
}
