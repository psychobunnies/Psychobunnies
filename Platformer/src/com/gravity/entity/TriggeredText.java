package com.gravity.entity;

/**
 * Represents a piece of text which can be triggered to display on screen.
 *
 * @author phulin
 */
public class TriggeredText {
    public final int x;
    public final int y;
    public final String text;

    private boolean triggered = false;

    public TriggeredText(int x, int y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public void trigger() {
        System.out.println("triggering");
        triggered = true;
    }

    public boolean isTriggered() {
        return triggered;
    }
}