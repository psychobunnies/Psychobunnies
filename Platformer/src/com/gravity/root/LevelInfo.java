package com.gravity.root;

public class LevelInfo {
    public final String title;
    public final String description;
    public final String mapfile;
    public final int stateId;

    public LevelInfo(String title, String description, String mapfile, int stateId) {
        this.title = title;
        this.description = description;
        this.mapfile = mapfile;
        this.stateId = stateId;
    }

    @Override
    public String toString() {
        return "LevelInfo [title=" + title + ", description=" + description + ", mapfile=" + mapfile + ", stateId=" + stateId + "]";
    }
}
