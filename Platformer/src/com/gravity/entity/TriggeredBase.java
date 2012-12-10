package com.gravity.entity;

import com.gravity.levels.UpdateCycling;

public abstract class TriggeredBase implements UpdateCycling {

    public final int x;
    public final int y;

    protected boolean triggered = false;

    public TriggeredBase(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void trigger() {
        triggered = true;
    }

    public boolean isTriggered() {
        return triggered;
    }

    @Override
    public void finishUpdate(float millis) {
        // No-op
    }

    @Override
    public void startUpdate(float millis) {
        triggered = false;
    }

}
