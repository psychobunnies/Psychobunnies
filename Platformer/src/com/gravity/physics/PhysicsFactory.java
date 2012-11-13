package com.gravity.physics;

public final class PhysicsFactory {

    private PhysicsFactory() {
        // never instantiated
    }

    private static final float DEFAULT_GRAVITY = 1.0f / 750f;
    public static final float DEFAULT_REHANDLE_BACKSTEP = -15f;
    public static final float DEFAULT_OFFSET_GROUND_CHECK = 2f;
    public static final float DEFAULT_ALLOWED_SIDE_OVERLAP = 0.5f;

    public static GravityPhysics createDefaultGravityPhysics(CollisionEngine engine) {
        return new GravityPhysics(engine, DEFAULT_GRAVITY, DEFAULT_OFFSET_GROUND_CHECK);
    }

    public static StopCollisionStrategy createDefaultStopCollisionStrategy() {
        return new StopCollisionStrategy(DEFAULT_ALLOWED_SIDE_OVERLAP);
    }

    public static RewindCollisionStrategy createDefaultRewindCollisionStrategy() {
        return new RewindCollisionStrategy(DEFAULT_REHANDLE_BACKSTEP);
    }

    public static SimplePhysics createSimplePhysics() {
        return new SimplePhysics();
    }

}
