package com.gravity.entity;

import org.newdawn.slick.Color;

import com.gravity.levels.UpdateCycling;

/**
 * Represents a piece of text which can be triggered to display on screen.
 * 
 * @author phulin
 */
public class TriggeredText implements UpdateCycling {
    public final int x;
    public final int y;
    public final Color color;
    public final String text;

    private boolean triggered = false;

    public TriggeredText(int x, int y, String text, Color color) {
        this.x = x;
        this.y = y;
        this.text = text.replaceAll("\\$", "\n");
        this.color = color;
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
