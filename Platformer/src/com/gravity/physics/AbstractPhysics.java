package com.gravity.physics;

public class AbstractPhysics {
    
    protected CollisionEngine collisionEngine;
    
    public AbstractPhysics(CollisionEngine collisionEngine) {
        this.collisionEngine = collisionEngine;
    }
    
}