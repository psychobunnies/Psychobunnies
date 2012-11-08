package com.gravity.physics;

import java.util.List;

import com.google.common.collect.Multimap;
import com.gravity.geom.Rect;

public interface CollisionEngine {

    public boolean addCollidable(Collidable collidable, Integer layer, boolean handlesCollisions);

    public boolean removeCollidable(Collidable entity);

    public void update(float millis);

    public Multimap<Collidable, RectCollision> computeCollisions(float time, boolean includePassThrough);

    public List<RectCollision> checkAgainstLayer(float time, Collidable collidable, Integer layer);

    public List<Collidable> collisionsInLayer(float millis, Rect rect, Integer layer);

}
