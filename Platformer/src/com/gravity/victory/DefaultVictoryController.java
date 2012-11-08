package com.gravity.victory;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.gravity.entity.VictoryTile;
import com.gravity.physics.Collidable;

public final class DefaultVictoryController implements VictoryController {

    /**
     * Each update cycle, this set is filled with all players. As each player is found to have hit a victory tile, its collidable is removed from this
     * set. If at the end of the cycle this set is empty, then victory has been achieved.
     */
    private final Set<Collidable> nonHits = Sets.newIdentityHashSet();

    private final List<Collidable> players;

    public DefaultVictoryController(List<Collidable> players) {
        this.players = players;
    }

    @Override
    public void startUpdate() {
        nonHits.clear();
        nonHits.addAll(players);
    }

    @Override
    public void collidableOnVictoryTile(Collidable entity) {
        nonHits.remove(entity);
    }

    @Override
    public boolean endUpdate() {
        return nonHits.isEmpty();
    }

    @Override
    public void control(List<VictoryTile> tiles) {
        for (VictoryTile t : tiles) {
            t.initialize(this);
        }
    }

}
