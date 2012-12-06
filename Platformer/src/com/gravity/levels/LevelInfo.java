package com.gravity.levels;

public class LevelInfo {
    static private final int STATE_ID_OFFSET = 1000;
    
    private static class NextOrder {
        private int nextOrder = 0;
        
        public synchronized int get() {
            nextOrder++;
            return nextOrder - 1;
        }
    }
    private static NextOrder nextOrder = new NextOrder();

    public final String title;
    public final String victoryText;
    public final String mapfile;
    public final int stateId;
    public final int levelOrder;

    /**
     * Constructor for standard levels (slingshot, bouncy, etc.)
     */
    public LevelInfo(String title, String description, String mapfile) {
        this.title = title;
        this.victoryText = description;
        this.mapfile = mapfile;
        this.levelOrder = nextOrder.get();
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
