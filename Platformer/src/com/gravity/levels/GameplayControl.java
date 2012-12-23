package com.gravity.levels;

import java.util.List;

import org.newdawn.slick.geom.Vector2f;

import com.gravity.fauna.Player;

/**
 * Interface which specifies any kind of logic that crosses between model, controller, and renderers.
 * 
 * @author xiao
 */
public interface GameplayControl {

    public void playerDies(Player player);

    public void playerHitSpikes(Player player);

    public void specialMoveSlingshot(Player slingshoter, float strength);

    public void playerFinishes(Player player);

    public void newStartPositions(List<Vector2f> startPositions);
}
