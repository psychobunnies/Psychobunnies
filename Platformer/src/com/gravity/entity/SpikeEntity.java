package com.gravity.entity;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.gravity.fauna.Player;
import com.gravity.gameplay.GravityGameController;
import com.gravity.geom.Rect;
import com.gravity.physics.Collidable;
import com.gravity.physics.RectCollision;

public final class SpikeEntity extends TileWorldEntity {
    
    private final GravityGameController controller;
    private final Set<Player> collidedPlayers = Sets.newIdentityHashSet();
    
    public SpikeEntity(GravityGameController controller, Rect shape) {
        super(shape);
        this.controller = controller;
    }
    
    @Override
    public void handleCollisions(float ticks, Collection<RectCollision> collisions) {
        for (RectCollision c : collisions) {
            Collidable e = c.getOtherEntity(this);
            if (e instanceof Player) {
                Player p = (Player) e;
                if (collidedPlayers.add(p)) {
                    controller.playerHitSpikes(p);
                }
            }
        }
        super.handleCollisions(ticks, collisions);
    }
    
}
