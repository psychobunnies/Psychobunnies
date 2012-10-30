package com.gravity.physics;

import java.util.EnumSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gravity.entity.TileWorldEntity;
import com.gravity.geom.Rect;
import com.gravity.geom.Rect.Side;

public class CollisionEngineTest {
    
    @Test
    public void testCornerCollisions() {
        Collidable a = new TileWorldEntity(new Rect(0, 0, 10, 5));
        Collidable b = new TileWorldEntity(new Rect(8, 3, 10, 5));
        
        CollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER, true);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER, true);
        
        List<RectCollision> collisions = engine.checkAgainstLayer(0, a, LayeredCollisionEngine.FAUNA_LAYER);
        Assert.assertTrue("Collisions size", collisions.size() == 1);
        Assert.assertEquals("Corner collision failed", EnumSet.of(Side.BOTTOM, Side.RIGHT), collisions.get(0));
    }
    
    @Test
    public void testCrossCollisions() {
        Collidable a = new TileWorldEntity(new Rect(0, 0, 10, 5));
        Collidable b = new TileWorldEntity(new Rect(-2, 3, 20, 1));
        
        CollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER, true);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER, true);
        
        List<RectCollision> collisions = engine.checkAgainstLayer(0, a, LayeredCollisionEngine.FAUNA_LAYER);
        Assert.assertTrue("Collisions size", collisions.size() == 1);
        Assert.assertEquals("Cross collision failed", EnumSet.of(Side.LEFT, Side.RIGHT), collisions.get(0));
    }
    
    @Test
    public void testSideCollisions() {
        Collidable a = new TileWorldEntity(new Rect(0, 0, 10, 5));
        Collidable b = new TileWorldEntity(new Rect(0, -3, 10, 5));
        
        CollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER, true);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER, true);
        
        List<RectCollision> collisions = engine.checkAgainstLayer(0, a, LayeredCollisionEngine.FAUNA_LAYER);
        Assert.assertTrue("Collisions size", collisions.size() == 1);
        Assert.assertEquals("Side collision failed", EnumSet.of(Side.TOP), collisions.get(0));
    }
    
    @Test
    public void testOverlapCollisions() {
        Collidable a = new TileWorldEntity(new Rect(0, 0, 10, 5));
        Collidable b = new TileWorldEntity(new Rect(-2, 4, 14, 10));
        
        CollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER, true);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER, true);
        
        List<RectCollision> collisions = engine.checkAgainstLayer(0, a, LayeredCollisionEngine.FAUNA_LAYER);
        Assert.assertTrue("Collisions size", collisions.size() == 1);
        Assert.assertEquals("Overlap collision failed", EnumSet.allOf(Side.class), collisions.get(0));
    }
    
}
