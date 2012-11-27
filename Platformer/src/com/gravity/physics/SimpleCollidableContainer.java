package com.gravity.physics;

import java.util.Set;

import com.google.common.collect.Sets;
import com.gravity.geom.Rect;

public final class SimpleCollidableContainer implements CollidableContainer {

    private final Set<Collidable> collidables = Sets.newIdentityHashSet();

    @Override
    public void addCollidable(Collidable collidable) {
        collidables.add(collidable);
    }

    @Override
    public boolean contains(Collidable collidable) {
        return collidables.contains(collidable);
    }

    @Override
    public boolean removeCollidable(Collidable collidable) {
        return collidables.remove(collidable);
    }

    @Override
    public Set<Collidable> getNearbyCollidables(Rect rect) {
        return collidables;
    }

    @Override
    public void update(float millis) {
        // no-op
    }

    @Override
    public boolean isEmpty() {
        return collidables.isEmpty();
    }

    @Override
    public Iterable<Collidable> collidables() {
        return collidables;
    }

    @Override
    public void clear() {
        collidables.clear();
    }

}
