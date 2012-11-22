package com.gravity.physics;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.gravity.geom.Rect;

public final class PartitionedCollidableContainer implements CollidableContainer {

    private final Map<Integer, CollidableContainer> partitions = Maps.newIdentityHashMap();
    private final SimpleCollidableContainer allCollidables = new SimpleCollidableContainer();

    private static final int PARTITION_SIZE = 64;

    /** how much to the left and right of a given collidable is considered "nearby" */
    private static final float BUFFER_ZONE_SIZE = 48f;

    private static final float REFRESH_INTERVAL = 1000f;

    private float elapsedTime = 0f;

    @Override
    public void addCollidable(Collidable collidable) {
        allCollidables.addCollidable(collidable);
        int start, end;
        Rect r = collidable.getRect(0f);
        start = getPartition(r.getX());
        end = getPartition(r.getMaxX());
        for (int i = start; i <= end; i++) {
            addToPartition(i, collidable);
        }
    }

    private void addToPartition(int part, Collidable collidable) {
        CollidableContainer cont = partitions.get(part);
        if (cont == null) {
            cont = new SimpleCollidableContainer();
            partitions.put(part, cont);
        }
        cont.addCollidable(collidable);
    }

    private int getPartition(float x) {
        return ((int) x / PARTITION_SIZE);
    }

    @Override
    public boolean contains(Collidable collidable) {
        return allCollidables.contains(collidable);
    }

    @Override
    public boolean removeCollidable(Collidable collidable) {
        boolean result = allCollidables.removeCollidable(collidable);
        if (result) {
            int start, end;
            Rect r = collidable.getRect(0f);
            start = getPartition(r.getX());
            end = getPartition(r.getMaxX());
            for (int i = start; i <= end; i++) {
                partitions.get(i).removeCollidable(collidable);
            }
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return allCollidables.isEmpty();
    }

    @Override
    public Iterable<Collidable> collidables() {
        return allCollidables.collidables();
    }

    @Override
    public Iterable<Collidable> getNearbyCollidables(Rect r) {
        int start, end;
        Set<Collidable> result = Sets.newIdentityHashSet();

        start = getPartition(r.getX() - BUFFER_ZONE_SIZE);
        end = getPartition(r.getMaxX() + BUFFER_ZONE_SIZE);
        for (int i = start; i <= end; i++) {
            CollidableContainer cont = partitions.get(i);
            if (cont != null) {
                for (Collidable c : cont.getNearbyCollidables(r)) {
                    result.add(c);
                }
            }
        }

        return result;
    }

    private void refresh() {
        partitions.clear();
        for (Collidable c : allCollidables.collidables()) {
            addCollidable(c);
        }
    }

    @Override
    public void update(float millis) {
        elapsedTime += millis;
        // TODO Predrag: This is slightly hacky, but I don't think there is a need to constantly update the container whenever a collidable moves
        if (elapsedTime >= REFRESH_INTERVAL) {
            refresh();
            elapsedTime = 0f;
        }
    }

    @Override
    public void clear() {
        allCollidables.clear();
        partitions.clear();
    }

}
