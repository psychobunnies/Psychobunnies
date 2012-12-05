package com.gravity.entity;

import org.newdawn.slick.Color;

/**
 * Represents a piece of text which can be triggered to display on screen.
 * 
 * @author phulin
 */
public class TriggeredText extends TriggeredBase {
    public final Color color;
    public final String text;

    public TriggeredText(int x, int y, String text, Color color) {
        super(x, y);
        this.text = text.replaceAll("\\$", "\n");
        this.color = color;
    }
}
