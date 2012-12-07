package com.gravity.map.tiles;

import org.newdawn.slick.Graphics;

import com.google.common.base.Preconditions;
import com.gravity.geom.Rect;

public final class CompositeTileRenderer implements TileRenderer {

    private final TileRenderer a, b;
    private final float cutoff;

    public CompositeTileRenderer(TileRenderer a, TileRenderer b, float cutoff) {
        Preconditions.checkArgument(cutoff <= 1f && cutoff >= 0f, "Cutoff must be between 0 and 1, but was " + cutoff);
        this.a = a;
        this.b = b;
        this.cutoff = cutoff;
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY, Rect location) {
        render(g, offsetX, offsetY, location, 0f);
    }

    @Override
    public void render(Graphics g, int offsetX, int offsetY, Rect location, float progress) {
        if (progress <= cutoff) {
            a.render(g, offsetX, offsetY, location, progress / cutoff);
        } else {
            b.render(g, offsetX, offsetY, location, Math.min(1, (progress - cutoff) / (1 - cutoff)));
        }
    }
}
