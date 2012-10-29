package com.gravity.physics;

public final class PhysicsUtils {
    
    private PhysicsUtils() {
        // never instantiated
    }
    
    private static final float DEFAULT_GRAVITY = 1.0f / 5000f;
    private static final float DEFAULT_REHANDLE_BACKSTEP = -15f;
    
    public static GravityPhysics createDefaultGravityPhysics(CollisionEngine engine) {
        return new GravityPhysics(engine, DEFAULT_GRAVITY, DEFAULT_REHANDLE_BACKSTEP);
    }
    
    public static SimplePhysics createSimplePhysics() {
        return new SimplePhysics();
    }
    
}
