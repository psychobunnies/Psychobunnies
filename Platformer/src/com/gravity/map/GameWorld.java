package com.gravity.map;

import java.util.List;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.entity.VictoryTile;
import com.gravity.physics.Collidable;
import com.gravity.root.Renderer;

public interface GameWorld extends Renderer {

    /** Initializes the GameWorld. Must be called before this object is used otherwise. */
    public void initialize();

    /** Get the height of this map, in pixels */
    public int getHeight();

    /** Get the width of this map, in pixels */
    public int getWidth();

    /** Return a list of entities for use in collision detection that do not wish to be notified of collisions */
    public List<Collidable> getTerrainEntitiesNoCalls();

    /** Return a list of entities for use in collision detection that wish to be notified of collisions */
    public List<Collidable> getTerrainEntitiesCallColls();

    public List<Vector2f> getPlayerStartPositions();

    public List<VictoryTile> getVictoryTiles();

}
