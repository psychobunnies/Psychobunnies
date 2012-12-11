package com.gravity.physics;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.geom.Vector2f;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.gravity.entity.TriggeredCollidable;
import com.gravity.geom.Rect;
import com.gravity.geom.Rect.Side;
import com.gravity.map.LevelFinishZone;

/**
 * Collision engine using the new Rect system. Also supports having multiple collision layers. Collidables within a layer will not have collisions
 * checked between them.
 * 
 * @author xiao
 * 
 */
public class LayeredCollisionEngine implements CollisionEngine {

    private static final float EPS = 1e-10f;
    private static final float CORNER_RELATIVE_DIFF = 1.001f;
    private static final float COLLISION_RELATIVE_DIFF = 1.001f;
    private static final float COLLISION_TIME_DIFF = 1.001f;

    public static final Integer FLORA_LAYER = 2;
    public static final Integer FAUNA_LAYER = 0;
    public static final Integer FALLING_LAYER = 1;

    private static final int PARTS_PER_TICK = 7;
    private static final float MIN_INCREMENT = 3f;

    private static final float MIN_ALLOWED_REPEATED_COLLISION_DELTA = 1e-4f;
    private static final float MAX_ALLOWED_REPEATS = 10;

    // package private for testing
    final Map<Integer, CollidableContainer> collidables;
    final Map<Collidable, Integer> layerMap;

    private boolean stopped = false;

    private final List<Integer> layers = Lists.newArrayList();

    public LayeredCollisionEngine() {
        collidables = Maps.newHashMapWithExpectedSize(3);
        layerMap = Maps.newIdentityHashMap();
    }

    /**
     * Add a collidable to the engine. If the collidable is already in the engine, it will be adjusted to the specified layer/handle flags.
     * 
     * @param layer
     *            The layer to add the collidable to - Collisions will only be checked with collidables from different layers.
     * 
     * @return true if the element was not already in the engine.
     */
    @Override
    public boolean addCollidable(Collidable collidable, Integer layer) {
        boolean retval = removeCollidable(collidable);

        if (!collidables.containsKey(layer)) {
            collidables.put(layer, new PartitionedCollidableContainer());
            layers.add(layer);
        }
        collidables.get(layer).addCollidable(collidable);
        layerMap.put(collidable, layer);
        return !retval;
    }

    /**
     * Remove a collidable from the engine.
     * 
     * @return true if the collidable was found and removed.
     */
    @Override
    public boolean removeCollidable(Collidable collidable) {
        if (layerMap.containsKey(collidable)) {
            Integer layer = layerMap.remove(collidable);
            collidables.get(layer).removeCollidable(collidable);
            if (collidables.get(layer).isEmpty()) {
                collidables.remove(layer);
                layers.remove(layer);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Given a collidable, check for any collisions with a specified layer
     * 
     * @param time
     *            time in millis since the last update call to check against.
     * @param layer
     *            the layer to check against
     * @return a list of collisions found
     */
    @Override
    public List<RectCollision> checkAgainstLayer(float time, Collidable collidable, Integer layer) {
        Preconditions.checkArgument(time > 0, "Time since last update() call must be nonnegative");

        List<RectCollision> colls = Lists.newLinkedList();
        boolean collides;
        Rect collidableRect = collidable.getPhysicalState().getRectangleAt(time);

        CollidableContainer cont = collidables.get(layer);
        if (cont != null) {
            for (Collidable collB : cont.getNearbyCollidables(collidableRect)) {
                collides = collidableRect.intersects(collB.getPhysicalState().getRectangleAt(time));
                if (collides) {
                    colls.add(getCollision(time, collidable, collB));
                }
            }
        }
        return colls;
    }

    /**
     * Given two collidables that are guaranteed to collide by the specified time, get the RectCollision object representing that collision.
     * 
     * @return a RectCollision object with a time set to the moment of collision.
     */
    public RectCollision getCollision(float time, Collidable collA, Collidable collB) {
        EnumSet<Side> sidesA;
        EnumSet<Side> sidesB;

        Rect rectA = collA.getPhysicalState().getRectangleAt(time);
        Rect rectB = collB.getPhysicalState().getRectangleAt(time);
        sidesB = rectB.getCollision(rectA);
        sidesA = rectA.getCollision(rectB);

        SidesAndTime a = getCollisionSide(time, collA, collB, sidesA);
        SidesAndTime b = getCollisionSide(time, collB, collA, sidesB);

        // Preconditions.checkArgument(!(collA.causesCollisionsWith(collB) && collB.causesCollisionsWith(collA)) || (Math.abs(a.time - b.time) < EPS),
        // "Collision time mismatch: a=" + a.toString() + "; b=" + b.toString() + "; collA=" + collA.toString() + "; collB=" + collB.toString());

        //@formatter:off
        return new RectCollision(collA, collB, Math.max(a.time, b.time), 
                a.sides,
                b.sides);
        //@formatter:on
    }

    /** Solves ax^2 + bx + c = 0, returns solutions as x, y components of a Vector2f */
    private Vector2f solveQuadratic(float a, float b, float c) {
        if (Math.abs(a) <= EPS) {
            return new Vector2f(-c / b, Float.NaN);
        } else {
            float delta = b * b - 4 * a * c;
            if (delta < -EPS) {
                // System.err.println("Warning: No solutions to quadratic found: a=" + a + "; b=" + b + "; c=" + c + "; delta=" + delta);
                return new Vector2f(Float.NaN, Float.NaN);
            } else if (delta <= EPS) {
                // delta = 0;
                return new Vector2f(-b / (2 * a), Float.NaN);
            } else {
                delta = (float) Math.sqrt(delta) / (2 * a);
                float p = -b / (2 * a);
                return new Vector2f(p - delta, p + delta);
            }
        }
    }

    /**
     * Gets the solutions of a quadratic from a float vector and returns the one that is between 0 and maxAllowed. Will return Float.NaN if no
     * solutions exist. Will throw an exception if both solutions are valid.
     **/
    private float getValidSolution(Vector2f solutions, float maxAllowed) {
        // increase upper bound slightly to avoid rounding-error-related bugs
        maxAllowed *= COLLISION_TIME_DIFF;
        float res;
        if (Float.isNaN(solutions.x) || solutions.x < 0 || solutions.x > maxAllowed) {
            res = solutions.y;
            if (Float.isNaN(res) || res < 0 || res > maxAllowed) {
                // System.out.println("No valid solutions found: maxAllowed=" + maxAllowed + "; solutions=" + solutions.toString());
                return Float.NaN;
            }
        } else {
            res = solutions.x;
            if (!(Float.isNaN(solutions.y) || solutions.y < 0 || solutions.y > maxAllowed)) {
                System.err.println("WARNING: Two valid solutions found: maxAllowed=" + maxAllowed + "; solutions=" + solutions.toString());
                // throw new RuntimeException("Two valid solutions found: maxAllowed=" + maxAllowed + "; solutions=" + solutions.toString());
                return Math.min(solutions.x, solutions.y);
            }
        }
        return res;
    }

    private SidesAndTime getCollisionSide(float time, Collidable a, Collidable b, EnumSet<Side> sidesA) {
        Vector2f avel, bvel, aacc, bacc, xSolnT, ySolnT;
        avel = a.getPhysicalState().getVelocity();
        bvel = b.getPhysicalState().getVelocity();
        avel.x += a.getPhysicalState().getSurfaceVelocityX();
        bvel.x += b.getPhysicalState().getSurfaceVelocityX();
        aacc = a.getPhysicalState().getAcceleration();
        bacc = b.getPhysicalState().getAcceleration();
        Rect arect, brect;
        arect = a.getPhysicalState().getRectangle();
        brect = b.getPhysicalState().getRectangle();
        float xRelV, yRelV, xRelA, yRelA, xDist, yDist, xTime, yTime;

        if (arect.intersects(brect) && a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) {
            System.err.println("Rectangles intersect at t=0! time=" + time + "; a=" + a.toString() + "; b=" + b.toString());
            return new SidesAndTime(sidesA, 0f);
        }

        // accel*t^2 + vel*t + dist = 0, for 0<t<time
        if (sidesA.contains(Side.RIGHT)) {
            if (sidesA.contains(Side.TOP)) {
                xRelV = avel.x - bvel.x;
                yRelV = bvel.y - avel.y;
                xRelA = aacc.x - bacc.x;
                yRelA = bacc.y - aacc.y;

                xDist = arect.getMaxX() - brect.getX();
                yDist = brect.getMaxY() - arect.getY();

                xSolnT = solveQuadratic(xRelA / 2, xRelV, xDist);
                ySolnT = solveQuadratic(yRelA / 2, yRelV, yDist);

                xTime = getValidSolution(xSolnT, time);
                yTime = getValidSolution(ySolnT, time);

                if (xRelV <= 0 && xRelV + xRelA * time <= 0) {
                    if (yRelV <= 0 && yRelV + yRelA * time <= 0) {
                        // objects are colliding while moving apart - probably TriggeredTextCollidable
                        return new SidesAndTime(sidesA, time);
                    } else {
                        // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(yTime));
                        if (Float.isNaN(yTime)) {
                            yTime = EPS;
                        }
                        return new SidesAndTime(EnumSet.of(Side.TOP), yTime);
                    }
                } else if (yRelV <= 0 && yRelV + yRelA * time <= 0) {
                    // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(xTime));
                    if (Float.isNaN(xTime)) {
                        xTime = EPS;
                    }
                    return new SidesAndTime(EnumSet.of(Side.RIGHT), xTime);
                }

                if (Float.isNaN(xTime)) {
                    // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(yTime));
                    if (Float.isNaN(yTime)) {
                        yTime = EPS;
                    }
                    return new SidesAndTime(EnumSet.of(Side.TOP), yTime);
                } else if (Float.isNaN(yTime)) {
                    return new SidesAndTime(EnumSet.of(Side.RIGHT), xTime);
                }

                if (xTime * CORNER_RELATIVE_DIFF < yTime) {
                    // x collision was first
                    return new SidesAndTime(EnumSet.of(Side.RIGHT), xTime);
                } else if (yTime * CORNER_RELATIVE_DIFF < xTime) {
                    // y collision was first
                    return new SidesAndTime(EnumSet.of(Side.TOP), yTime);
                } else {
                    System.out.println("Corner collision found: " + a.toString() + " from " + b.toString());
                    return new SidesAndTime(sidesA, Math.min(xTime, yTime));
                }
            } else if (sidesA.contains(Side.BOTTOM)) {
                xRelV = avel.x - bvel.x;
                yRelV = avel.y - bvel.y;
                xRelA = aacc.x - bacc.x;
                yRelA = aacc.y - bacc.y;

                xDist = arect.getMaxX() - brect.getX();
                yDist = arect.getMaxY() - brect.getY();

                xSolnT = solveQuadratic(xRelA / 2, xRelV, xDist);
                ySolnT = solveQuadratic(yRelA / 2, yRelV, yDist);

                xTime = getValidSolution(xSolnT, time);
                yTime = getValidSolution(ySolnT, time);

                if (xRelV <= 0 && xRelV + xRelA * time <= 0) {
                    if (yRelV <= 0 && yRelV + yRelA * time <= 0) {
                        // objects are colliding while moving apart - probably TriggeredTextCollidable
                        return new SidesAndTime(sidesA, time);
                    } else {
                        // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(yTime));
                        if (Float.isNaN(yTime)) {
                            yTime = EPS;
                        }
                        return new SidesAndTime(EnumSet.of(Side.BOTTOM), yTime);
                    }
                } else if (yRelV <= 0 && yRelV + yRelA * time <= 0) {
                    // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(xTime));
                    if (Float.isNaN(xTime)) {
                        xTime = EPS;
                    }
                    return new SidesAndTime(EnumSet.of(Side.RIGHT), xTime);
                }

                if (Float.isNaN(xTime)) {
                    // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(yTime));
                    if (Float.isNaN(yTime)) {
                        yTime = EPS;
                    }
                    return new SidesAndTime(EnumSet.of(Side.BOTTOM), yTime);
                } else if (Float.isNaN(yTime)) {
                    return new SidesAndTime(EnumSet.of(Side.RIGHT), xTime);
                }

                if (xTime * CORNER_RELATIVE_DIFF < yTime) {
                    // x collision was first
                    return new SidesAndTime(EnumSet.of(Side.RIGHT), xTime);
                } else if (yTime * CORNER_RELATIVE_DIFF < xTime) {
                    // y collision was first
                    return new SidesAndTime(EnumSet.of(Side.BOTTOM), yTime);
                } else {
                    System.out.println("Corner collision found: " + a.toString() + " from " + b.toString());
                    return new SidesAndTime(sidesA, Math.min(xTime, yTime));
                }
            } else if (sidesA.contains(Side.LEFT)) {
                yRelV = avel.y - bvel.y;
                yRelA = aacc.y - bacc.y;
                yDist = arect.getMaxY() - brect.getY();

                float tmp = yRelV * yDist;
                if (tmp > 0) {
                    ySolnT = solveQuadratic(yRelA / 2, yRelV, yDist);
                    yTime = getValidSolution(ySolnT, time);
                    // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(yTime));
                    if (Float.isNaN(yTime)) {
                        yTime = EPS;
                    }
                    return new SidesAndTime(EnumSet.of(Side.BOTTOM), yTime);
                } else if (tmp < 0) {
                    yRelV = bvel.y - avel.y;
                    yRelA = bacc.y - aacc.y;
                    yDist = brect.getMaxY() - arect.getY();
                    ySolnT = solveQuadratic(yRelA / 2, yRelV, yDist);
                    yTime = getValidSolution(ySolnT, time);
                    // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(yTime));
                    if (Float.isNaN(yTime)) {
                        yTime = EPS;
                    }
                    return new SidesAndTime(EnumSet.of(Side.TOP), yTime);
                } else {
                    if (a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) {
                        System.out.println("Weird collision found " + a.toString() + " from " + b.toString());
                    }
                    return new SidesAndTime(sidesA, time);
                }
            } else {
                // simple right collision occurred
                xRelV = avel.x - bvel.x;
                xRelA = aacc.x - bacc.x;
                xDist = arect.getMaxX() - brect.getX();

                xSolnT = solveQuadratic(xRelA / 2, xRelV, xDist);
                xTime = getValidSolution(xSolnT, time);

                // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(xTime),
                // "No solutions for collision found: time=" + time + "; a=" + a.toString() + "; b=" + b.toString());
                if (Float.isNaN(xTime)) {
                    xTime = EPS;
                }
                return new SidesAndTime(sidesA, xTime);
            }
        } else if (sidesA.contains(Side.LEFT)) {
            if (sidesA.contains(Side.TOP)) {
                xRelV = bvel.x - avel.x;
                yRelV = bvel.y - avel.y;
                xRelA = bacc.x - aacc.x;
                yRelA = bacc.y - aacc.y;

                xDist = brect.getMaxX() - arect.getX();
                yDist = brect.getMaxY() - arect.getY();

                xSolnT = solveQuadratic(xRelA / 2, xRelV, xDist);
                ySolnT = solveQuadratic(yRelA / 2, yRelV, yDist);

                xTime = getValidSolution(xSolnT, time);
                yTime = getValidSolution(ySolnT, time);

                if (xRelV <= 0 && xRelV + xRelA * time <= 0) {
                    if (yRelV <= 0 && yRelV + yRelA * time <= 0) {
                        // objects are colliding while moving apart - probably TriggeredTextCollidable
                        return new SidesAndTime(sidesA, time);
                    } else {
                        // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(yTime));
                        if (Float.isNaN(yTime)) {
                            yTime = EPS;
                        }
                        return new SidesAndTime(EnumSet.of(Side.TOP), yTime);
                    }
                } else if (yRelV <= 0 && yRelV + yRelA * time <= 0) {
                    // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(xTime));
                    if (Float.isNaN(xTime)) {
                        xTime = EPS;
                    }
                    return new SidesAndTime(EnumSet.of(Side.LEFT), xTime);
                }

                if (Float.isNaN(xTime)) {
                    // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(yTime));
                    if (Float.isNaN(yTime)) {
                        yTime = EPS;
                    }
                    return new SidesAndTime(EnumSet.of(Side.TOP), yTime);
                } else if (Float.isNaN(yTime)) {
                    return new SidesAndTime(EnumSet.of(Side.LEFT), xTime);
                }

                if (xTime * CORNER_RELATIVE_DIFF < yTime) {
                    // x collision was first
                    return new SidesAndTime(EnumSet.of(Side.LEFT), xTime);
                } else if (yTime * CORNER_RELATIVE_DIFF < xTime) {
                    // y collision was first
                    return new SidesAndTime(EnumSet.of(Side.TOP), yTime);
                } else {
                    System.out.println("Corner collision found: " + a.toString() + " from " + b.toString());
                    return new SidesAndTime(sidesA, Math.min(xTime, yTime));
                }
            } else if (sidesA.contains(Side.BOTTOM)) {
                xRelV = bvel.x - avel.x;
                yRelV = avel.y - bvel.y;
                xRelA = bacc.x - aacc.x;
                yRelA = aacc.y - bacc.y;

                xDist = brect.getMaxX() - arect.getX();
                yDist = arect.getMaxY() - brect.getY();

                xSolnT = solveQuadratic(xRelA / 2, xRelV, xDist);
                ySolnT = solveQuadratic(yRelA / 2, yRelV, yDist);

                xTime = getValidSolution(xSolnT, time);
                yTime = getValidSolution(ySolnT, time);

                if (xRelV <= 0 && xRelV + xRelA * time <= 0) {
                    if (yRelV <= 0 && yRelV + yRelA * time <= 0) {
                        // objects are colliding while moving apart - probably TriggeredTextCollidable
                        return new SidesAndTime(sidesA, time);
                    } else {
                        // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(yTime));
                        if (Float.isNaN(yTime)) {
                            yTime = EPS;
                        }
                        return new SidesAndTime(EnumSet.of(Side.BOTTOM), yTime);
                    }
                } else if (yRelV <= 0 && yRelV + yRelA * time <= 0) {
                    // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(xTime));
                    if (Float.isNaN(xTime)) {
                        xTime = EPS;
                    }
                    return new SidesAndTime(EnumSet.of(Side.LEFT), xTime);
                }

                if (Float.isNaN(xTime)) {
                    // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(yTime));
                    if (Float.isNaN(yTime)) {
                        yTime = EPS;
                    }
                    return new SidesAndTime(EnumSet.of(Side.BOTTOM), yTime);
                } else if (Float.isNaN(yTime)) {
                    return new SidesAndTime(EnumSet.of(Side.LEFT), xTime);
                }

                if (xTime * CORNER_RELATIVE_DIFF < yTime) {
                    // x collision was first
                    return new SidesAndTime(EnumSet.of(Side.LEFT), xTime);
                } else if (yTime * CORNER_RELATIVE_DIFF < xTime) {
                    // y collision was first
                    return new SidesAndTime(EnumSet.of(Side.BOTTOM), yTime);
                } else {
                    System.out.println("Corner collision found: " + a.toString() + " from " + b.toString());
                    return new SidesAndTime(sidesA, Math.min(xTime, yTime));
                }
            } else {
                // simple left collision occurred
                xRelV = bvel.x - avel.x;
                xRelA = bacc.x - aacc.x;
                xDist = brect.getMaxX() - arect.getX();

                xSolnT = solveQuadratic(xRelA / 2, xRelV, xDist);
                xTime = getValidSolution(xSolnT, time);

                // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(xTime),
                // "No solutions for collision found: time=" + time + "; a=" + a.toString() + "; b=" + b.toString());
                if (Float.isNaN(xTime)) {
                    xTime = EPS;
                }
                return new SidesAndTime(sidesA, xTime);
            }
        } else if (sidesA.contains(Side.TOP)) {
            if (sidesA.contains(Side.BOTTOM)) {
                xRelV = avel.x - bvel.x;
                xRelA = aacc.x - bacc.x;
                xDist = arect.getMaxX() - brect.getX();

                float tmp = xRelV * xDist;
                if (tmp > 0) {
                    xSolnT = solveQuadratic(xRelA / 2, xRelV, xDist);
                    xTime = getValidSolution(xSolnT, time);
                    // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(xTime));
                    if (Float.isNaN(xTime)) {
                        xTime = EPS;
                    }
                    return new SidesAndTime(EnumSet.of(Side.RIGHT), xTime);
                } else if (tmp < 0) {
                    xRelV = bvel.x - avel.x;
                    xRelA = bacc.x - aacc.x;
                    xDist = brect.getMaxX() - arect.getX();
                    xSolnT = solveQuadratic(xRelA / 2, xRelV, xDist);
                    xTime = getValidSolution(xSolnT, time);
                    // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(xTime));
                    if (Float.isNaN(xTime)) {
                        xTime = EPS;
                    }
                    return new SidesAndTime(EnumSet.of(Side.LEFT), xTime);
                } else {
                    if (a.causesCollisionsWith(b) && b.causesCollisionsWith(a))
                        System.out.println("Weird collision found: " + a.toString() + " from " + b.toString());
                }
                return new SidesAndTime(sidesA, time);
            } else {
                // simple top collision occurred
                yRelV = bvel.y - avel.y;
                yRelA = bacc.y - aacc.y;
                yDist = brect.getMaxY() - arect.getY();

                ySolnT = solveQuadratic(yRelA / 2, yRelV, yDist);
                yTime = getValidSolution(ySolnT, time);

                // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(yTime),
                // "No solutions for collision found: time=" + time + "; a=" + a.toString() + "; b=" + b.toString());
                if (Float.isNaN(yTime)) {
                    yTime = EPS;
                }
                return new SidesAndTime(sidesA, yTime);
            }
        }

        // simple bottom collision occurred
        yRelV = avel.y - bvel.y;
        yRelA = aacc.y - bacc.y;
        yDist = arect.getMaxY() - brect.getY();

        ySolnT = solveQuadratic(yRelA / 2, yRelV, yDist);
        yTime = getValidSolution(ySolnT, time);

        // Preconditions.checkArgument(!(a.causesCollisionsWith(b) && b.causesCollisionsWith(a)) || !Float.isNaN(yTime),
        // "No solutions for collision found: time=" + time + "; a=" + a.toString() + "; b=" + b.toString());
        if (Float.isNaN(yTime)) {
            yTime = EPS;
        }
        return new SidesAndTime(sidesA, yTime);
    }

    @Override
    public List<Collidable> collisionsInLayer(float time, Rect rect, Integer layer, boolean ignoreTextAndFinish) {
        Preconditions.checkArgument(time >= 0, "Time since last update() call must be nonnegative");

        boolean collides;
        List<Collidable> result = Lists.newArrayList();
        for (Collidable collB : collidables.get(layer).getNearbyCollidables(rect)) {
            if (ignoreTextAndFinish && (collB instanceof TriggeredCollidable || collB instanceof LevelFinishZone)) {
                continue;
            }
            collides = rect.intersects(collB.getPhysicalState().getRectangleAt(time));
            if (collides) {
                result.add(collB);
            }
        }
        return result;
    }

    @Override
    public Multimap<Collidable, RectCollision> computeCollisions(float time) {
        Preconditions.checkArgument(time > 0, "Time since last update() call must be nonnegative");

        Multimap<Collidable, RectCollision> collList = HashMultimap.create();
        List<RectCollision> colls;

        int size = layers.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                CollidableContainer cont = collidables.get(layers.get(i));
                if (cont != null) {
                    for (Collidable collA : cont.collidables()) {
                        colls = checkAgainstLayer(time, collA, layers.get(j));
                        for (RectCollision coll : colls) {
                            if (coll.entityA.causesCollisionsWith(coll.entityB)) {
                                collList.put(coll.entityB, coll);
                            }
                            if (coll.entityB.causesCollisionsWith(coll.entityA)) {
                                collList.put(coll.entityA, coll);
                            }
                        }
                    }
                }
            }
        }

        return collList;
    }

    @Override
    public void update(float millis) {
        float last = Float.NEGATIVE_INFINITY;
        int counter = 0;
        boolean forceRehandle = false;
        float time;
        float increment = Math.max(MIN_INCREMENT, millis / PARTS_PER_TICK);

        Preconditions.checkArgument(millis >= 0, "Time since last update() call must be nonnegative");

        for (CollidableContainer cont : collidables.values()) {
            cont.update(millis);
        }

        if (millis > MIN_INCREMENT) {
            for (time = increment; !stopped && time < millis; time += increment) {
                time = runCollisionsAndHandling(time, false, forceRehandle);
                if (last + MIN_ALLOWED_REPEATED_COLLISION_DELTA > time) {
                    counter++;
                    if (counter >= MAX_ALLOWED_REPEATS) {
                        forceRehandle = true;
                    } else {
                        forceRehandle = false;
                    }
                } else {
                    last = time;
                    forceRehandle = false;
                    counter = 0;
                }
            }
        }

        time = 0;
        while (!stopped && time < millis) {
            time = runCollisionsAndHandling(millis, false, forceRehandle);
            if (last + MIN_ALLOWED_REPEATED_COLLISION_DELTA > time) {
                counter++;
                if (counter >= MAX_ALLOWED_REPEATS) {
                    forceRehandle = true;
                } else {
                    forceRehandle = false;
                }
            } else {
                last = time;
                forceRehandle = false;
                counter = 0;
            }
        }
    }

    private float runCollisionsAndHandling(float millis, boolean report, boolean forceRehandle) {
        Multimap<Collidable, RectCollision> collisions;
        collisions = computeCollisions(millis);
        if (collisions.isEmpty()) {
            if (report) {
                System.err.println("No collisions!");
            }
            return millis;
        }

        if (report) {
            System.err.println("Collisions!");
        }

        float smallest = millis;
        float cutoff;
        for (RectCollision coll : collisions.values()) {
            if (coll.entityA.causesCollisionsWith(coll.entityB) && coll.entityB.causesCollisionsWith(coll.entityA)) {
                smallest = Math.min(smallest, coll.time + EPS);
            }
        }
        cutoff = smallest * COLLISION_RELATIVE_DIFF;
        List<RectCollision> collisionsToReport = Lists.newArrayList();
        for (Collidable collidable : collisions.keySet()) {
            collisionsToReport.clear();
            for (RectCollision coll : collisions.get(collidable)) {
                if (coll.time <= cutoff) {
                    collisionsToReport.add(coll);
                }
            }
            if (forceRehandle) {
                System.out.println("Force rehandling collisions at t=" + cutoff + " between " + collidable.toString() + " and "
                        + collisionsToReport.toString());
                collidable.rehandleCollisions(cutoff, collisionsToReport);
            } else {
                collidable.handleCollisions(cutoff, collisionsToReport);
            }
        }

        if (stopped) {
            return Float.NaN;
        }

        collisions = computeCollisions(cutoff);
        if (collisions.isEmpty()) {
            return Math.min(millis, cutoff);
        }
        for (Collidable collidable : collisions.keySet()) {
            collidable.rehandleCollisions(cutoff, collisions.get(collidable));
        }

        if (stopped) {
            return Float.NaN;
        }

        collisions = computeCollisions(cutoff);
        for (Collidable collidable : collisions.keySet()) {
            for (RectCollision collision : collisions.get(collidable)) {
                Collidable other = collision.getOtherEntity(collidable);
                if (other.causesCollisionsWith(collidable) && collidable.causesCollisionsWith(other)) {
                    // Die if we still have any "real" collisions.
                    // TODO: fix this - should separate things that can overlap from things that want to be notified of overlap
                    throw new RuntimeException("Could not rehandle collisions at time " + millis + "; forceRehandle=" + forceRehandle + "; stopped="
                            + stopped + "; collisions=: " + collisions);
                }
            }
        }
        return Math.min(millis, cutoff);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        //@formatter:off
        builder.append("<---LayeredCollisionEngine[\n") 
                         .append(" * collidables=").append(collidables).append("\n")
                         .append(" * layerMap=").append(layerMap).append("\n")
                         .append("--->");
        //@formatter:on
        return builder.toString();
    }

    private class SidesAndTime {
        final EnumSet<Side> sides;
        final float time;

        public SidesAndTime(EnumSet<Side> sides, float time) {
            this.sides = sides;
            this.time = time;
        }

        @Override
        public String toString() {
            return "SidesAndTime [sides=" + sides + ", time=" + time + "]";
        }

    }

    @Override
    public void stop() {
        stopped = true;
    }
}
