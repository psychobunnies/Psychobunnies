package com.gravity.physics;

import com.gravity.geom.Rect;

public interface CollidableContainer {

    public void addCollidable(Collidable collidable);

    public boolean contains(Collidable collidable);

    public boolean removeCollidable(Collidable collidable);

    public boolean isEmpty();

    /**
     * Returns an iterable of all collidables in this container
     */
    public Iterable<Collidable> collidables();

    /**
     * Returns an iterable of collidables that will be near the given rectangle at the given time point.
     */
    public Iterable<Collidable> getNearbyCollidables(Rect rect);

    public void update(float millis);

    public void clear();

}
