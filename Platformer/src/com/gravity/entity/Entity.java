package com.gravity.entity;

import com.gravity.physics.Collidable;
import com.gravity.root.UpdateCycling;

/**
 * Represents a collidable object which moves in the world.
 * 
 * @author xiao
 */
public interface Entity extends UpdateCycling, PhysicallyStateful, Collidable {
}
