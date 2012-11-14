package com.gravity.physics;

import java.util.EnumSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gravity.geom.Rect;
import com.gravity.geom.Rect.Side;
import com.gravity.map.StaticCollidable;

public class CollisionEngineTest {

    @Test
    public void testCornerCollisions() {
        Collidable a = new StaticCollidable(new Rect(0, 0, 10, 5));
        Collidable b = new StaticCollidable(new Rect(8, 3, 10, 5));

        CollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER);

        List<RectCollision> collisions = engine.checkAgainstLayer(1, a, LayeredCollisionEngine.FAUNA_LAYER);
        Assert.assertTrue("Collisions size", collisions.size() == 1);
        Assert.assertEquals("Corner collision failed", EnumSet.of(Side.BOTTOM, Side.RIGHT), collisions.get(0).getMyCollisions(a));
    }

    @Test
    public void testCrossCollisions() {
        Collidable a = new StaticCollidable(new Rect(0, 0, 10, 5));
        Collidable b = new StaticCollidable(new Rect(-2, 3, 20, 1));

        CollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER);

        List<RectCollision> collisions = engine.checkAgainstLayer(1, a, LayeredCollisionEngine.FAUNA_LAYER);
        Assert.assertTrue("Collisions size", collisions.size() == 1);
        Assert.assertEquals("Cross collision failed", EnumSet.of(Side.LEFT, Side.RIGHT), collisions.get(0).getMyCollisions(a));
    }

    @Test
    public void testSideCollisions() {
        Collidable a = new StaticCollidable(new Rect(0, 0, 10, 5));
        Collidable b = new StaticCollidable(new Rect(0, -3, 10, 5));

        CollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER);

        List<RectCollision> collisions = engine.checkAgainstLayer(1, a, LayeredCollisionEngine.FAUNA_LAYER);
        Assert.assertTrue("Collisions size", collisions.size() == 1);
        Assert.assertEquals("Side collision failed", EnumSet.of(Side.TOP), collisions.get(0).getMyCollisions(a));
    }

    @Test
    public void testOverlapCollisions() {
        Collidable a = new StaticCollidable(new Rect(0, 0, 10, 5));
        Collidable b = new StaticCollidable(new Rect(-2, -4, 14, 10));

        CollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER);

        List<RectCollision> collisions = engine.checkAgainstLayer(1, a, LayeredCollisionEngine.FAUNA_LAYER);
        Assert.assertTrue("Collisions size", collisions.size() == 1);
        Assert.assertEquals("Overlap collision failed", EnumSet.allOf(Side.class), collisions.get(0).getMyCollisions(a));
    }

    @Test
    public void testCollisionFinder() {
        Collidable a = new VelocityCollidable(new Rect(0, 0, 1, 1), .1f, 0);
        Collidable b = new VelocityCollidable(new Rect(3, 0.5f, 1, 1), -.1f, 0);

        LayeredCollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER);

        RectCollision collision = engine.getCollision(20, a, b);
        Assert.assertEquals(EnumSet.of(Side.RIGHT), collision.getMyCollisions(a));
        Assert.assertEquals(EnumSet.of(Side.LEFT), collision.getMyCollisions(b));
        Assert.assertTrue(Math.abs(collision.time - 10) > 0.1);
    }

    @Test
    public void testCollisionFinderHarder() {
        Collidable a = new VelocityCollidable(new Rect(0, 0, 1, 1), .2f, .4f);
        Collidable b = new VelocityCollidable(new Rect(1.5f, 4, 1, 1), .1f, .2f);

        LayeredCollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER);

        RectCollision collision = engine.getCollision(20, a, b);
        Assert.assertEquals(EnumSet.of(Side.BOTTOM), collision.getMyCollisions(a));
        Assert.assertEquals(EnumSet.of(Side.TOP), collision.getMyCollisions(b));
        Assert.assertTrue(Math.abs(collision.time - 5) > 0.1);
    }
}
