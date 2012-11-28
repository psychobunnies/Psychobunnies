package com.gravity.physics;

public final class PhysicsFactory {

    private PhysicsFactory() {
        // never instantiated
    }

    public static final float DEFAULT_GRAVITY = 1.0f / 1150f; // 750 before change
    public static final float DEFAULT_GROUND_FRICTION = 1.0f / 500f;
    public static final float DEFAULT_FRICTION_STOP_CUTOFF = 1.0f / 200f;
    public static final float DEFAULT_FRICTION_ACCEL_RATIO = 50f;
    public static final float DEFAULT_REHANDLE_BACKSTEP = -15f;
    public static final float DEFAULT_OFFSET_GROUND_CHECK = 5f;
    public static final float DEFAULT_MOVING_FEATHER = 2f;

    public static GravityPhysics createDefaultGravityPhysics(CollisionEngine engine) {
        return new GravityPhysics(engine, DEFAULT_GRAVITY, DEFAULT_REHANDLE_BACKSTEP, DEFAULT_OFFSET_GROUND_CHECK, DEFAULT_GROUND_FRICTION,
                DEFAULT_FRICTION_STOP_CUTOFF, DEFAULT_FRICTION_ACCEL_RATIO, DEFAULT_MOVING_FEATHER);
    }

    public static SimplePhysics createSimplePhysics() {
        return new SimplePhysics();
    }

}
