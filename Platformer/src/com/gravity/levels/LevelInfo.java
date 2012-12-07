package com.gravity.levels;

public class LevelInfo {
    static private final int STATE_ID_OFFSET = 1000;

    public final String title;
    public final String victoryText;
    public final String mapfile;
    public final int stateId;
    public final int levelOrder;

    /**
     * Constructor for standard levels (slingshot, bouncy, etc.)
     */
    public LevelInfo(String title, String description, String mapfile, int levelOrder) {
        this.title = title;
        this.victoryText = description;
        this.mapfile = mapfile;
        this.levelOrder = levelOrder;
        this.stateId = this.levelOrder + STATE_ID_OFFSET;
    }

    /**
     * Constructor for non standard levels (main menu, pause screen, etc.)
     */
    public LevelInfo(String title, String mapFile, int stateId) {
        this.title = title;
        this.victoryText = "";
        this.mapfile = mapFile;
        this.stateId = stateId;
        this.levelOrder = -1;
    }

    @Override
    public String toString() {
        return "LevelInfo [title=" + title + ", description=" + victoryText + ", mapfile=" + mapfile + ", stateId=" + stateId + "]";
    }
}
