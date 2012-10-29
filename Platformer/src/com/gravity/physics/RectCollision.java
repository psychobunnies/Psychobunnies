package com.gravity.physics;

import java.util.EnumSet;

import com.gravity.geom.Rect.Side;

/**
 * Represents a collision at a certain point in time between two entities <br>
 * Contains the time of the collision, and the set of vertices in each shape which were involved with the collision.
 * 
 * @author xiao
 */
public class RectCollision {
    
    public final Collidable entityA, entityB;
    public final float time;
    public final EnumSet<Side> sidesA, sidesB;
    
    public RectCollision(Collidable entityA, Collidable entityB, float time, EnumSet<Side> sidesA, EnumSet<Side> sidesB) {
        this.entityA = entityA;
        this.entityB = entityB;
        this.time = time;
        this.sidesA = sidesA;
        this.sidesB = sidesB;
    }
    
    /** Get the other entity in the collision */
    public Collidable getOtherEntity(Collidable me) {
        if (me == entityA) {
            return entityB;
        } else {
            return entityA;
        }
    }
    
    /**
     * Get the indices of the vertices in my shape which collided with the other entity.
     */
    public EnumSet<Side> getMyCollisions(Collidable me) {
        if (me == entityA) {
            return sidesA;
        } else {
            return sidesB;
        }
    }
    
    /**
     * Get the indices of the vertices in the other's shape which collided with my entity.
     */
    public EnumSet<Side> getOtherCollisions(Collidable me) {
        if (me == entityA) {
            return sidesB;
        } else {
            return sidesA;
        }
    }
    
    @Override
    public String toString() {
        return "Collision [A=" + entityA + ", B=" + entityB + ", time=" + time + ", sidesA=" + sidesA + ", sidesB=" + sidesB + "]";
    }
    
}
