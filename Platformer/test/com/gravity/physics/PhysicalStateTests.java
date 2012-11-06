package com.gravity.physics;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import com.gravity.geom.Rect;

public final class PhysicalStateTests {

    private static final float EPS = 1e-6f;

    @Test
    public void testPhysicalStateSnapshotPositive() {
        float posX = 1;
        float posY = 2;
        float velX = 3;
        float velY = 4;
        float accX = 5;
        float accY = 6;

        float time = 2;
        float expectedPosX = 17;
        float expectedPosY = 22;
        float expectedVelX = 13;
        float expectedVelY = 16;
        float expectedAccX = 5;
        float expectedAccY = 6;

        PhysicalState state = new PhysicalState(new Rect(posX, posY, 1, 1), velX, velY, accX, accY);
        PhysicalState newState = state.snapshot(time);
        assertEquals(expectedPosX, newState.getPosition().x, EPS);
        assertEquals(expectedPosY, newState.getPosition().y, EPS);
        assertEquals(expectedVelX, newState.velX, EPS);
        assertEquals(expectedVelY, newState.velY, EPS);
        assertEquals(expectedAccX, newState.accX, EPS);
        assertEquals(expectedAccY, newState.accY, EPS);
    }

    @Test
    public void testPhysicalStateSnapshotNegative() {
        float posX = 1;
        float posY = 2;
        float velX = 3;
        float velY = 4;
        float accX = 5;
        float accY = 6;

        float time = -2;
        float expectedPosX = -15;
        float expectedPosY = -18;
        float expectedVelX = -7;
        float expectedVelY = -8;
        float expectedAccX = 5;
        float expectedAccY = 6;

        PhysicalState state = new PhysicalState(new Rect(posX, posY, 1, 1), velX, velY, accX, accY);
        PhysicalState newState = state.snapshot(time);
        assertEquals(expectedPosX, newState.getPosition().x, EPS);
        assertEquals(expectedPosY, newState.getPosition().y, EPS);
        assertEquals(expectedVelX, newState.velX, EPS);
        assertEquals(expectedVelY, newState.velY, EPS);
        assertEquals(expectedAccX, newState.accX, EPS);
        assertEquals(expectedAccY, newState.accY, EPS);
    }
}
