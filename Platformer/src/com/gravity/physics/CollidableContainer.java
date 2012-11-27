package com.gravity.physics;

import java.util.Set;

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
     * Returns a set of collidables that will be near the given rectangle at the given time point.
     */
    public Set<Collidable> getNearbyCollidables(Rect rect);

    public void update(float millis);

    public void clear();

}
