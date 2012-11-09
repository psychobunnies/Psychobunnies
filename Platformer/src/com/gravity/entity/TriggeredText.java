package com.gravity.entity;

import com.gravity.root.UpdateCycling;

/**
 * Represents a piece of text which can be triggered to display on screen.
 * 
 * @author phulin
 */
public class TriggeredText implements UpdateCycling {
    public final int x;
    public final int y;
    public final String text;

    private boolean triggered = false;

    public TriggeredText(int x, int y, String text) {
        this.x = x;
        this.y = y;
        this.text = text.replaceAll("\\$", "\n");
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
