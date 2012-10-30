package com.gravity.physics;

public final class PhysicsFactory {

    private PhysicsFactory() {
        // never instantiated
    }

    private static final float DEFAULT_GRAVITY = 1.0f / 750f;
    private static final float DEFAULT_REHANDLE_BACKSTEP = -15f;

    public static GravityPhysics createDefaultGravityPhysics(CollisionEngine engine) {
        return new GravityPhysics(engine, DEFAULT_GRAVITY);
    }

    public static SimplePhysics createSimplePhysics() {
        return new SimplePhysics();
    }

}
