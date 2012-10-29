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
import com.gravity.geom.Rect.Side;

/**
 * Collision engine using the new Shape system. Also supports having multiple collision layers. Collidibles within a layer will not have collisions
 * checked between them.
 * 
 * @author xiao
 * 
 */
public class LayeredCollisionEngine {
    public static final Integer FLORA_LAYER = 0;
    public static final Integer FAUNA_LAYER = 1;
    
    private final Map<Integer, Set<Collidable>> collidables;
    private final Map<Collidable, Integer> layerMap;
    private final Set<Collidable> callMap;
    
    public LayeredCollisionEngine() {
        collidables = Maps.newHashMapWithExpectedSize(2);
        layerMap = Maps.newIdentityHashMap();
        callMap = Sets.newIdentityHashSet();
    }
    
    /**
     * Add a collidable to the engine. If the collidable is already in the engine, it will be adjusted to the specified layer/handle flags.
     * 
     * @param layer
     *            The layer to add the collidable to - Collisions will only be checked with collidables from different layers.
     * @param handlesCollisions
     *            whether or not the collidable will have its {@link Collidable#handleCollisions(float, java.util.List)} method called.
     * @return true if the element was not already in the engine.
     */
    public boolean addCollidable(Collidable collidable, Integer layer, boolean handlesCollisions) {
        boolean retval = removeCollidable(collidable);
        if (collidables.containsKey(layer)) {
            collidables.put(layer, Sets.<Collidable> newIdentityHashSet());
        }
        collidables.get(layer).add(collidable);
        if (handlesCollisions) {
            callMap.add(collidable);
        }
        layerMap.put(collidable, layer);
        return retval;
    }
    
    /**
     * Remove a collidable from the engine.
     * 
     * @return true if the collidable was found and removed.
     */
    public boolean removeCollidable(Collidable collidable) {
        if (layerMap.containsKey(collidable)) {
            Integer layer = layerMap.remove(collidable);
            callMap.remove(collidable);
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
    public List<RectCollision> checkAgainstLayer(float time, Collidable collidable, Integer layer) {
        Preconditions.checkArgument(time >= 0, "Time since last update() call must be nonnegative");
        
        List<RectCollision> colls = Lists.newLinkedList();
        EnumSet<Side> sidesA, sidesB;
        
        for (Collidable collB : collidables.get(layer)) {
            sidesA = collidable.getRect(time).getCollision(collB.getRect(time));
            if (!sidesA.isEmpty()) {
                sidesB = collB.getRect(time).getCollision(collidable.getRect(time));
                colls.add(new RectCollision(collidable, collB, time, sidesA, sidesB));
            }
        }
        return colls;
    }
    
    public Multimap<Collidable, RectCollision> computeCollisions(float time) {
        Preconditions.checkArgument(time >= 0, "Time since last update() call must be nonnegative");
        
        Multimap<Collidable, RectCollision> collList = HashMultimap.create();
        List<RectCollision> colls;
        Set<Integer> layers = collidables.keySet();
        EnumSet<Side> sidesA, sidesB;
        int size = layers.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                for (Collidable collA : collidables.get(i)) {
                    colls = checkAgainstLayer(time, collA, j);
                    for (RectCollision coll : colls) {
                        collList.put(coll.entityA, coll);
                        collList.put(coll.entityB, coll);
                    }
                }
            }
        }
        
        return collList;
    }
    
    private static final int PARTS_PER_TICK = 5;
    private static final int MIN_INCREMENT = 10;
    
    public void update(float millis) {
        Preconditions.checkArgument(millis >= 0, "Time since last update() call must be nonnegative");
        Multimap<Collidable, RectCollision> collisions;
        
        float increment = Math.max(MIN_INCREMENT, millis / PARTS_PER_TICK);
        for (float time = increment; time < millis; time += increment) {
            collisions = computeCollisions(time);
            if (collisions.isEmpty()) {
                continue;
            }
            for (Collidable coll : collisions.keySet()) {
                if (callMap.contains(coll)) {
                    coll.handleCollisions(millis, collisions.get(coll));
                }
            }
            
            collisions = computeCollisions(time);
            if (collisions.isEmpty()) {
                continue;
            }
            for (Collidable coll : collisions.keySet()) {
                if (callMap.contains(coll)) {
                    coll.rehandleCollisions(millis, collisions.get(coll));
                }
            }
            
            collisions = computeCollisions(time);
            if (collisions.isEmpty()) {
                continue;
            }
            throw new RuntimeException("Could not rehandle collisions: " + collisions);
        }
    }
}
