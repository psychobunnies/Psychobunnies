package com.gravity.physics;

import java.util.Set;

import com.gravity.entity.Entity;

/**
 * Represents a collision at a certain point in time between two entities <br>
 * Contains the time of the collision, and the set of vertices in each shape which were involved with the collision.
 * <ul>
 * <li>If entityA's corner collided with entityB, then entityA's collisions set will have a single integer, denoting the vertex number.
 * <li>If entityB's side collided eith entityA, then entityB's collisions set will have two integers, one for each end of the side.
 * </ul>
 * 
 * @deprecated
 * @author xiao
 */
@Deprecated
public class Collision {
    
    public final Entity entityA, entityB;
    public final float time;
    public final Set<Integer> collisionsA, collisionsB;
    
    public Collision(Entity entityA, Entity entityB, float time, Set<Integer> collisionA, Set<Integer> collisionB) {
        this.entityA = entityA;
        this.entityB = entityB;
        this.time = time;
        this.collisionsA = collisionA;
        this.collisionsB = collisionB;
    }
    
    /** Get the other entity in the collision */
    public Entity getOtherEntity(Entity me) {
        if (me == entityA) {
            return entityB;
        } else {
            return entityA;
        }
    }
    
    /**
     * Get the indices of the vertices in my shape which collided with the other entity.
     */
    public Set<Integer> getMyCollisions(Entity me) {
        if (me == entityA) {
            return collisionsA;
        } else {
            return collisionsB;
        }
    }
    
    /**
     * Get the indices of the vertices in the other's shape which collided with my entity.
     */
    public Set<Integer> getOtherCollisions(Entity me) {
        if (me == entityA) {
            return collisionsB;
        } else {
            return collisionsA;
        }
    }
    
    @Override
    public String toString() {
        return "Collision [entityA=" + entityA + ", entityB=" + entityB + ", time=" + time + ", collisionsA=" + collisionsA + ", collisionsB=" + collisionsB + "]";
    }
    
}
