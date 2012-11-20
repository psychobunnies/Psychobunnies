package com.gravity.map;

import java.util.List;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.Layer;

import com.gravity.geom.Rect;
import com.gravity.physics.Collidable;
import com.gravity.physics.CollisionEngine;
import com.gravity.root.Renderer;

public interface GameWorld extends Renderer {

    public static final String INVISIBLE_TIME_PROPERTY = "invisible_time";
    public static final String NORMAL_VISIBLE_TIME_PROPERTY = "normal_visible_time";
    public static final String FLICKER_TIME_PROPERTY = "flicker_time";
    public static final String GEOMETRIC_PARAMETER_PROPERTY = "geometric";
    public static final String FLICKER_COUNT_PROPERTY = "flicker_count";

    public static final Vector2f PLAYER_ONE_DEFAULT_STARTPOS = new Vector2f(256, 512);
    public static final Vector2f PLAYER_TWO_DEFAULT_STARTPOS = new Vector2f(224, 512);

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

    /** Return a list of player start positions, in order from first to nth player */
    public List<Vector2f> getPlayerStartPositions();

    /** Return the goal rectangle */
    public Rect getFinishRect();

    /** Return the layer with the given name */
    public Layer getLayer(String name);

    /** Reinitializes the disappearing layers with the given collision engine */
    public List<DisappearingTileController> reinitializeDisappearingLayers(CollisionEngine engine);

}
