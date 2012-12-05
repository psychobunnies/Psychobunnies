package com.gravity.map.tiles;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.gravity.fauna.Player;
import com.gravity.geom.Rect;
import com.gravity.levels.GameplayControl;
import com.gravity.map.StaticCollidable;
import com.gravity.physics.Collidable;
import com.gravity.physics.RectCollision;
import com.gravity.root.GameSounds;
import com.gravity.root.GameSounds.Event;

public final class SpikeEntity extends StaticCollidable {

    private final GameplayControl controller;
    private final Set<Player> collidedPlayers = Sets.newIdentityHashSet();

    public SpikeEntity(GameplayControl controller, Rect shape) {
        super(shape);
        this.controller = controller;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SpikeEntity [super=");
        builder.append(super.toString());
        builder.append(", collidedPlayers=");
        builder.append(collidedPlayers);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public void handleCollisions(float ticks, Collection<RectCollision> collisions) {
        for (RectCollision c : collisions) {
            Collidable e = c.getOtherEntity(this);
            if (e instanceof Player) {
                Player p = (Player) e;
                if (collidedPlayers.add(p)) {
                    controller.playerHitSpikes(p);
                    GameSounds.playSoundFor(Event.SPIKED);
                }
            }
        }
        super.handleCollisions(ticks, collisions);
    }
}
