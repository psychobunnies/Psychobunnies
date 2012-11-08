package com.gravity.victory;

import java.util.List;

import com.gravity.entity.VictoryTile;
import com.gravity.physics.Collidable;

public interface VictoryController {

    /** Must be called at the start of each update cycle to reset VictoryTile hits. */
    public void startUpdate();

    /** Called every time a collidable collides with a VictoryTile under this controller's supervision. */
    public void collidableOnVictoryTile(Collidable entity);

    /** Returns true if the players have won during this update cycle. */
    public boolean endUpdate();

    /** Calls each tile's initialize() method to set its controller to this object. */
    public void control(List<VictoryTile> tiles);

}
