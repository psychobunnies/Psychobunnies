package com.gravity.entity;

import com.gravity.levels.UpdateCycling;
import com.gravity.physics.Collidable;

/**
 * Represents a collidable object which moves in the world.
 * 
 * @author xiao
 */
public interface Entity extends UpdateCycling, Collidable {

    public void unavoidableCollisionFound();

}
