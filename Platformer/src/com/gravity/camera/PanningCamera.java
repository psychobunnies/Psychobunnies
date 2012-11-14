package com.gravity.camera;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.geom.Rect;
import com.gravity.root.UpdateCycling;

/** A camera that pans at a constant rate */
public class PanningCamera implements Camera, UpdateCycling {

    private final float velX, velY;
    private final float delay;
    private final float height, width;
    private final float stopX, stopY;
    private float curX, curY;
    private float time;

    public PanningCamera(float delay, Vector2f start, Vector2f vel, Vector2f finish, float width, float height) {
        this.delay = delay;
        this.velX = -vel.x;
        this.velY = -vel.y;
        this.curX = -start.x;
        this.curY = -start.y;
        this.stopX = -finish.x;
        this.stopY = -finish.y;
        this.width = width;
        this.height = height;
        this.time = 0;
    }

    @Override
    public Rect getViewport() {
        return new Rect(curX, curY, width, height);
    }

    @Override
    public void finishUpdate(float millis) {
        time += millis;
        if (time > delay && curX >= stopX && curY >= stopY) {
            curX += millis * velX;
            curY += millis * velY;
        }
    }

    @Override
    public void startUpdate(float millis) {
        // No-op
    }

}
