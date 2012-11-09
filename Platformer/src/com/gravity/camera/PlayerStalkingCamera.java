package com.gravity.camera;

import java.util.Arrays;
import java.util.Set;

import org.newdawn.slick.geom.Vector2f;

import com.google.common.collect.Sets;
import com.gravity.fauna.Player;
import com.gravity.geom.Rect;

public class PlayerStalkingCamera implements Camera {

    Set<Player> players;
    Rect bounding;
    float width;
    float height;

    public PlayerStalkingCamera(float width, float height, Vector2f max, Vector2f min, Player... players) {
        this.players = Sets.newIdentityHashSet();
        this.players.addAll(Arrays.asList(players));
        this.width = width;
        this.height = height;
        bounding = new Rect(min.x, min.y, max.x - width, max.y - height);
    }

    @Override
    public Rect getViewport() {
        Vector2f pos = new Vector2f();
        for (Player player : players) {
            pos.add(player.getPosition(0));
        }
        pos.scale(1.0f / players.size());
        float x = pos.x + width / 2;
        float y = pos.y + height / 2;
        x = Math.max(x, bounding.getX());
        y = Math.max(y, bounding.getY());
        x = Math.min(x, bounding.getMaxX());
        y = Math.min(y, bounding.getMaxY());
        return new Rect(-x, -y, width, height);
    }

}
