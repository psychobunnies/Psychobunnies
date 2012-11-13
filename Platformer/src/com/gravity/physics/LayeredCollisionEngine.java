package com.gravity.physics;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.gravity.geom.Rect;
import com.gravity.geom.Rect.Side;

/**
 * Collision engine using the new Rect system. Also supports having multiple collision layers. Collidables within a layer will not have collisions
 * checked between them.
 * 
 * @author xiao
 * 
 */
public class LayeredCollisionEngine implements CollisionEngine {
    private static final float EPS = 1e-6f;
    private static final float TIME_GRAN = 3e-2f;
    private static final float PIXEL_GRAN = 1e-1f;
    public static final Integer FLORA_LAYER = 1;
    public static final Integer FAUNA_LAYER = 0;

    // package private for testing
    final Map<Integer, Set<Collidable>> collidables;
    final Map<Collidable, Integer> layerMap;

    public LayeredCollisionEngine() {
        collidables = Maps.newHashMapWithExpectedSize(2);
        layerMap = Maps.newIdentityHashMap();
    }

    /**
     * Add a collidable to the engine. If the collidable is already in the engine, it will be adjusted to the specified layer/handle flags.
     * 
     * @param layer
     *            The layer to add the collidable to - Collisions will only be checked with collidables from different layers.
     * 
     * @return true if the element was not already in the engine.
     */
    @Override
    public boolean addCollidable(Collidable collidable, Integer layer) {
        boolean retval = removeCollidable(collidable);
        if (!collidables.containsKey(layer)) {
            collidables.put(layer, Sets.<Collidable> newIdentityHashSet());
        }
        collidables.get(layer).add(collidable);
        layerMap.put(collidable, layer);
        return retval;
    }

    /**
     * Remove a collidable from the engine.
     * 
     * @return true if the collidable was found and removed.
     */
    @Override
    public boolean removeCollidable(Collidable collidable) {
        if (layerMap.containsKey(collidable)) {
            Integer layer = layerMap.remove(collidable);
            collidables.get(layer).remove(collidable);
            if (collidables.get(layer).isEmpty()) {
                collidables.remove(layer);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Given a collidable, check for any collisions with a specified layer
     * 
     * @param time
     *            time in millis since the last update call to check against.
     * @param layer
     *            the layer to check against
     * @return a list of collisions found
     */
    @Override
    public List<RectCollision> checkAgainstLayer(float time, Collidable collidable, Integer layer) {
        Preconditions.checkArgument(time > 0, "Time since last update() call must be nonnegative");

        List<RectCollision> colls = Lists.newLinkedList();
        EnumSet<Side> sidesA, sidesB;

        for (Collidable collB : collidables.get(layer)) {
            sidesA = collidable.getRect(time).getCollision(collB.getRect(time));
            if (!sidesA.isEmpty()) {
                colls.add(getCollision(time, collidable, collB));
            }
        }
        return colls;
    }

    /**
     * Given two collidables that are guaranteed to collide by the specified time, get the RectCollision object representing that collision.
     * 
     * @return a RectCollision object with a time set to the moment of collision.
     */
    public RectCollision getCollision(float time, Collidable collA, Collidable collB) {
        float upper = time;
        float lower = 0;
        float mid = (upper + lower) / 2;
        EnumSet<Side> sidesA = null;
        EnumSet<Side> sidesB;
        while (upper - lower >= TIME_GRAN) {
            sidesA = collA.getRect(mid).getCollision(collB.getRect(mid));
            if (sidesA.isEmpty()) { // No collision, time forward
                lower = mid;
                mid = (upper + lower) / 2;
            } else {
                upper = mid;
                mid = (upper + lower) / 2;
            }
        }
        Rect rectA = collA.getRect(upper);
        Rect rectB = collB.getRect(upper);
        sidesB = rectB.getCollision(rectA);
        //@formatter:off
        return new RectCollision(collA, collB, time, 
                getSmallestCollisionSide(rectA, rectB, sidesA),
                getSmallestCollisionSide(rectB, rectA, sidesB));
        //@formatter:on
    }

    private EnumSet<Side> getSmallestCollisionSide(Rect rectA, Rect rectB, EnumSet<Side> sidesA) {
        Side minSide = null;
        float minDist = Float.MAX_VALUE, curDist = minDist;
        for (Side side : sidesA) {
            switch (side) {
            case TOP:
                curDist = rectB.getMaxY() - rectA.getY();
                break;
            case BOTTOM:
                curDist = rectA.getMaxY() - rectB.getY();
                break;
            case LEFT:
                curDist = rectB.getMaxX() - rectA.getX();
                break;
            case RIGHT:
                curDist = rectA.getMaxX() - rectB.getX();
                break;
            }
            if (curDist < minDist) {
                minSide = side;
                minDist = curDist;
            }
        }
        // If there is no side with a small distance, ignore
        if (minDist > PIXEL_GRAN) {
            return sidesA;
        } else {
            return EnumSet.of(minSide);
        }
    }

    @Override
    public List<Collidable> collisionsInLayer(float time, Rect rect, Integer layer) {
        Preconditions.checkArgument(time >= 0, "Time since last update() call must be nonnegative");

        EnumSet<Side> sidesA;
        List<Collidable> result = Lists.newArrayList();
        for (Collidable collB : collidables.get(layer)) {
            sidesA = rect.getCollision(collB.getRect(time));
            if (!sidesA.isEmpty()) {
                result.add(collB);
            }
        }
        return result;
    }

    @Override
    public Multimap<Collidable, RectCollision> computeCollisions(float time) {
        Preconditions.checkArgument(time >= 0, "Time since last update() call must be nonnegative");

        Multimap<Collidable, RectCollision> collList = HashMultimap.create();
        List<RectCollision> colls;
        Set<Integer> layers = collidables.keySet();
        int size = layers.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                for (Collidable collA : collidables.get(i)) {
                    colls = checkAgainstLayer(time, collA, j);
                    for (RectCollision coll : colls) {
                        if (coll.entityA.causesCollisionsWith(coll.entityB)) {
                            collList.put(coll.entityB, coll);
                        }
                        if (coll.entityB.causesCollisionsWith(coll.entityA)) {
                            collList.put(coll.entityA, coll);
                        }
                    }
                }
            }
        }

        return collList;
    }

    private static final int PARTS_PER_TICK = 5;
    private static final int MIN_INCREMENT = 10;

    @Override
    public void update(float millis) {
        Preconditions.checkArgument(millis >= 0, "Time since last update() call must be nonnegative");

        if (millis > MIN_INCREMENT) {
            float increment = Math.max(MIN_INCREMENT, millis / PARTS_PER_TICK);
            float time;
            for (time = increment; time <= millis; time += increment) {
                runCollisionsAndHandling(time);
            }
        }
        runCollisionsAndHandling(millis);
    }

    private void runCollisionsAndHandling(float millis) {
        Multimap<Collidable, RectCollision> collisions;
        collisions = computeCollisions(millis);
        if (collisions.isEmpty()) {
            return;
        }
        for (Collidable collidable : collisions.keySet()) {
            collidable.handleCollisions(millis, collisions.get(collidable));
        }

        collisions = computeCollisions(millis);
        if (collisions.isEmpty()) {
            return;
        }
        for (Collidable collidable : collisions.keySet()) {
            collidable.rehandleCollisions(millis, collisions.get(collidable));
        }

        collisions = computeCollisions(millis);
        for (Collidable collidable : collisions.keySet()) {
            for (RectCollision collision : collisions.get(collidable)) {
                Collidable other = collision.getOtherEntity(collidable);
                if (other.causesCollisionsWith(collidable) && collidable.causesCollisionsWith(other)) {
                    // Die if we still have any "real" collisions.
                    // TODO: fix this - should separate things that can overlap from things that want to be notified of overlap
                    throw new RuntimeException("Could not rehandle collisions: " + collisions);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        //@formatter:off
        builder.append("<---LayeredCollisionEngine[\n") 
                         .append(" * collidables=").append(collidables).append("\n")
                         .append(" * layerMap=").append(layerMap).append("\n")
                         .append("--->");
        //@formatter:on
        return builder.toString();
    }
}
