package com.gravity.physics;

import java.util.EnumSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gravity.entity.Entity;
import com.gravity.entity.PhysicsEntity;
import com.gravity.geom.Rect;
import com.gravity.geom.Rect.Side;
import com.gravity.map.TileWorldCollidable;

public class CollisionEngineTest {

    @Test
    public void testCornerCollisions() {
        Collidable a = new TileWorldCollidable(new Rect(0, 0, 10, 5));
        Collidable b = new TileWorldCollidable(new Rect(8, 3, 10, 5));
        CollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER, true);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER, true);

        List<RectCollision> collisions = engine.checkAgainstLayer(0, a, LayeredCollisionEngine.FAUNA_LAYER);
        Assert.assertTrue("Collisions size", collisions.size() == 1);
        Assert.assertEquals("Corner collision failed", EnumSet.of(Side.BOTTOM, Side.RIGHT), collisions.get(0).getMyCollisions(a));
    }

    @Test
    public void testCrossCollisions() {
        Collidable a = new TileWorldCollidable(new Rect(0, 0, 10, 5));
        Collidable b = new TileWorldCollidable(new Rect(-2, 3, 20, 1));
        CollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER, true);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER, true);

        List<RectCollision> collisions = engine.checkAgainstLayer(0, a, LayeredCollisionEngine.FAUNA_LAYER);
        Assert.assertTrue("Collisions size", collisions.size() == 1);
        Assert.assertEquals("Cross collision failed", EnumSet.of(Side.LEFT, Side.RIGHT), collisions.get(0).getMyCollisions(a));
    }

    @Test
    public void testSideCollisions() {
        Collidable a = new TileWorldCollidable(new Rect(0, 0, 10, 5));
        Collidable b = new TileWorldCollidable(new Rect(0, -3, 10, 5));
        CollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER, true);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER, true);

        List<RectCollision> collisions = engine.checkAgainstLayer(0, a, LayeredCollisionEngine.FAUNA_LAYER);
        Assert.assertTrue("Collisions size", collisions.size() == 1);
        Assert.assertEquals("Side collision failed", EnumSet.of(Side.TOP), collisions.get(0).getMyCollisions(a));
    }

    @Test
    public void testOverlapCollisions() {
        Collidable a = new TileWorldCollidable(new Rect(0, 0, 10, 5));
        Collidable b = new TileWorldCollidable(new Rect(-2, -4, 14, 10));
        CollisionEngine engine = new LayeredCollisionEngine();
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER, true);
        engine.addCollidable(b, LayeredCollisionEngine.FAUNA_LAYER, true);

        List<RectCollision> collisions = engine.checkAgainstLayer(0, a, LayeredCollisionEngine.FAUNA_LAYER);
        Assert.assertTrue("Collisions size", collisions.size() == 1);
        Assert.assertEquals("Overlap collision failed", EnumSet.allOf(Side.class), collisions.get(0).getMyCollisions(a));
    }

    // @Test
    public void testFalling() {
        CollisionEngine engine = new LayeredCollisionEngine();

        Collidable a = new TileWorldCollidable(new Rect(0, -100, 100, 100));

        PhysicalState state = new PhysicalState(new Rect(0, 11, 1, 1), 0, 0);
        Entity ent = new PhysicsEntity<GravityPhysics>(state, PhysicsFactory.createDefaultGravityPhysics(engine)) {

        };

        engine.addCollidable(ent, LayeredCollisionEngine.FAUNA_LAYER, true);
        engine.addCollidable(a, LayeredCollisionEngine.FLORA_LAYER, true);

        float timestep = 10f;
        for (int i = 0; i < 2; i++) {
            ent.startUpdate(timestep);
            engine.update(timestep);
            ent.finishUpdate(timestep);
            System.out.println(ent.getPhysicalState());
        }
    }

}
