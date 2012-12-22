package com.gravity.map;

import junit.framework.Assert;

import org.junit.Test;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMapPlus;

import com.gravity.root.TestSlick;

public class LevelInfoTests {

    static public TestSlick testGame;
    static public AppGameContainer appGame;

    @Test
    public void testDummySlick() throws SlickException {
        new TestSlick() {
            @Override
            public void init(GameContainer container) throws SlickException {
            }
        }.run();
    }

    @Test
    public void testTileInfo() throws SlickException {
        new TestSlick() {
            @Override
            public void init(GameContainer container) throws SlickException {
                TiledMapPlus map = new TiledMapPlus("new-assets/levels/tutorial.tmx");
                int layerIndex = map.getLayerIndex("collisions");
                Assert.assertEquals(TileType.GROUND_MID, TileType.toTileType(map, 0, 0, layerIndex));
            }
        }.run();
    }
}
