package com.gravity.geom;

import java.util.EnumSet;

import junit.framework.Assert;

import org.junit.Test;
import org.newdawn.slick.geom.Vector2f;

import com.gravity.geom.Rect.Corner;
import com.gravity.geom.Rect.Side;

public class RectTest {
    
    @Test
    public void testEquals() {
        Assert.assertEquals(new Rect(0, 1, 2, 3), new Rect(0, 1, 2, 3));
    }
    
    @Test
    public void testGetPoint() {
        Rect rect = new Rect(0, 1, 2, 3);
        Assert.assertEquals("getPoint top left failed", new Vector2f(0, 1), rect.getPoint(Corner.TOPLEFT));
        Assert.assertEquals("getPoint top right failed", new Vector2f(2, 1), rect.getPoint(Corner.TOPRIGHT));
        Assert.assertEquals("getPoint bot left failed", new Vector2f(0, 4), rect.getPoint(Corner.BOTLEFT));
        Assert.assertEquals("getPoint bot right failed", new Vector2f(2, 4), rect.getPoint(Corner.BOTRIGHT));
        Assert.assertEquals("getCenter failed", new Vector2f(1, 2.5f), rect.getCenter());
    }
    
    @Test
    public void testContains() {
        Rect rect = new Rect(0, 1, 2, 3);
        Assert.assertFalse("Point (0, 0) should not be contained", rect.contains(0, 0));
        Assert.assertTrue("Point (1, 2) should be contained", rect.contains(1, 2));
    }
    
    @Test
    public void testScale() {
        Rect rect = new Rect(0, 1, 2, 4);
        Assert.assertEquals(new Rect(-1, -1, 4, 8), rect.scale(2));
    }
    
    @Test
    public void testTranslate() {
        Rect rect = new Rect(0, 1, 2, 4);
        Assert.assertEquals(new Rect(3, 3, 2, 4), rect.translate(3, 2));
    }
    
    @Test
    public void testIntersects() {
        Rect rect = new Rect(0, 1, 2, 4);
        Assert.assertTrue(rect.intersects(new Rect(1, 2, 2, 4)));
        Assert.assertFalse(rect.intersects(new Rect(2, 2, 2, 4)));
    }
    
    @Test
    public void testGetCollision() {
        Rect rect = new Rect(0, 1, 2, 4);
        Assert.assertEquals("Corner collision failed", EnumSet.of(Side.LEFT, Side.TOP), rect.getCollision(new Rect(-1, 0, 2, 4)));
        Assert.assertEquals("Side small collision failed", EnumSet.of(Side.RIGHT), rect.getCollision(new Rect(1, 2, 2, 2)));
        Assert.assertEquals("Side large collision failed", EnumSet.of(Side.BOTTOM), rect.getCollision(new Rect(-1, 3, 4, 3)));
        Assert.assertEquals("Cross collision failed", EnumSet.of(Side.BOTTOM, Side.TOP), rect.getCollision(new Rect(0.5f, 0, 1, 6)));
        Assert.assertEquals("Inside collision failed", EnumSet.allOf(Side.class), rect.getCollision(new Rect(0.5f, 2, 1, 2)));
        Assert.assertEquals("Outside collision failed", EnumSet.allOf(Side.class), rect.getCollision(new Rect(-1, 0, 4, 6)));
    }
}
