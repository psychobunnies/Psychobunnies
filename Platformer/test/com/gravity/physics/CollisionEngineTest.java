package com.gravity.physics;

import org.junit.Assert;
import org.junit.Test;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import com.gravity.entity.TileWorldEntity;


public class CollisionEngineTest {
    
    @Test
    public void testCornerCollisions() {
        Shape a = new Rectangle(0, 0, 10, 5);
        Shape b = new Rectangle(8, 3, 10, 5);
        
        Collision collision = StandardCollisionEngine.collisionLines(new TileWorldEntity(a), new TileWorldEntity(b), 0);
        Assert.assertArrayEquals(new Integer[] { 2 }, collision.collisionsA.toArray());
        Assert.assertArrayEquals(new Integer[] { 0 }, collision.collisionsB.toArray());
    }
    
    @Test
    public void testSideCollisions() {
        Shape a = new Rectangle(0, 0, 10, 5);
        Shape b = new Rectangle(0, 3, 10, 5);
        
        Collision collision = StandardCollisionEngine.collisionLines(new TileWorldEntity(a), new TileWorldEntity(b), 0);
        Assert.assertArrayEquals(new Integer[] { 2, 3 }, collision.collisionsA.toArray());
        Assert.assertArrayEquals(new Integer[] { 0, 1 }, collision.collisionsB.toArray());
    }
    
    @Test
    public void testTouchCollisions() {
        Shape a = new Rectangle(0, 0, 10, 5);
        Shape b = new Rectangle(-2, 4, 14, 9);
        
        Collision collision = StandardCollisionEngine.collisionLines(new TileWorldEntity(a), new TileWorldEntity(b), 0);
        Assert.assertArrayEquals(new Integer[] { 2, 3 }, collision.collisionsA.toArray());
        Assert.assertArrayEquals(new Integer[] { 0, 1 }, collision.collisionsB.toArray());
    }
    
}
