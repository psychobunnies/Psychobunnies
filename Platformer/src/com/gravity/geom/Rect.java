package com.gravity.geom;

import java.util.EnumSet;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import com.google.common.base.Preconditions;

/**
 * A rectangle with sides parallel to axes. Custom built, because the one in slick has bugs and performance issues.
 * 
 * @author xiao
 * 
 */
public class Rect {
    private static final float EPS = 1e-4f;

    private final float x, y;
    private final float height, width;

    /**
     * Enum representing the corners of the rectangle.
     * <ul>
     * <li>TOPLEFT is (x, y)
     * <li>BOTRIGHT is (x + width, y + height)
     * </ul>
     * 
     * @author xiao
     */
    public static enum Corner {
        TOPLEFT, TOPRIGHT, BOTLEFT, BOTRIGHT;

        public EnumSet<Side> getSides() {
            return EnumSet.of(getVertical(), getHorizontal());
        }

        public Side getVertical() {
            switch (this) {
            case TOPLEFT:
            case TOPRIGHT:
                return Side.TOP;
            case BOTLEFT:
            case BOTRIGHT:
                return Side.BOTTOM;
            default:
                throw new RuntimeException("Bad corner");
            }
        }

        public Side getHorizontal() {
            switch (this) {
            case TOPLEFT:
            case BOTLEFT:
                return Side.LEFT;
            case TOPRIGHT:
            case BOTRIGHT:
                return Side.RIGHT;
            default:
                throw new RuntimeException("Bad corner");
            }
        }
    }

    /**
     * Enum representing the sides of the rectangle.
     * <ul>
     * <li>TOP is (x, y) to (x + width, y)
     * <li>RIGHT is (x + width, y) to (x + width, y + height)
     * </ul>
     * 
     * @author xiao
     * 
     */
    public static enum Side {
        TOP, LEFT, BOTTOM, RIGHT;

        /**
         * Checks whether a set contains no opposing sides.
         * 
         * @param set
         *            Set to check
         * @return Whether or not set is "simple."
         */
        public static boolean isSimpleSet(EnumSet<Side> set) {
            if (set.size() == 1) {
                return true;
            } else {
                if (set.size() == 4) {
                    return false;
                } else {
                    if (set.equals(EnumSet.of(TOP, BOTTOM))) {
                        return false;
                    } else if (set.equals(EnumSet.of(LEFT, RIGHT))) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }

        public Side getOpposite() {
            switch (this) {
            case TOP:
                return BOTTOM;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            case BOTTOM:
                return TOP;
            default:
                throw new RuntimeException("Bad side");
            }
        }

        public static EnumSet<Side> opposite(EnumSet<Side> set) {
            if (isSimpleSet(set)) {
                if (set.size() == 1) {
                    switch (set.iterator().next()) {
                    case BOTTOM:
                        return EnumSet.of(Side.TOP);
                    case LEFT:
                        return EnumSet.of(Side.RIGHT);
                    case RIGHT:
                        return EnumSet.of(Side.LEFT);
                    case TOP:
                        return EnumSet.of(Side.BOTTOM);
                    default:
                        return set;
                    }
                } else {
                    return EnumSet.complementOf(set);
                }
            } else {
                return set;
            }
        }

    }

    public Rect(Shape rect) {
        x = rect.getMinX();
        y = rect.getMinY();
        width = rect.getWidth();
        height = rect.getHeight();
    }

    public Rect(float x, float y, float width, float height) {
        Preconditions.checkArgument(height >= 0, "Height must be positive");
        Preconditions.checkArgument(width >= 0, "Width must be positive");
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public Rect(Rect rect) {
        this.x = rect.x;
        this.y = rect.y;
        this.height = rect.height;
        this.width = rect.width;
    }

    public Vector2f getPoint(Corner index) {
        switch (index) {
        case TOPLEFT:
            return new Vector2f(x, y);
        case TOPRIGHT:
            return new Vector2f(x + width, y);
        case BOTLEFT:
            return new Vector2f(x, y + height);
        case BOTRIGHT:
            return new Vector2f(x + width, y + height);
        default:
            return new Vector2f(x, y);
        }
    }

    /** Given a point (px, py), return if the point is in the rectangle */
    public boolean contains(float px, float py) {
        return px >= x && px - x <= width && py >= y && py - y <= height;
    }

    /** Return a new Rect scaled by c about the Rectangle's center */
    public Rect scale(float c) {
        //@formatter:off
        return new Rect(x + (1 - c) * width / 2, y + (1 - c) * height / 2, 
                        width*c, height*c);
        //@formatter:on
    }

    /** Return a new Rect scaled about the Rectangle's center by xScale in the x direction and by yScale in the y direction. */
    public Rect scale(float xScale, float yScale) {
        //@formatter:off
        return new Rect(x + (1 - xScale) * width / 2, y + (1 - yScale) * height / 2, 
                        width*xScale, height*yScale);
        //@formatter:on
    }

    /** Returns a new Rect which is this Rect translated by (x, y) */
    public Rect translate(float tx, float ty) {
        return new Rect(x + tx, y + ty, width, height);
    }

    /** Returns a new Rect which is this Rect translated by (x, y) */
    public Rect translate(Vector2f v) {
        return new Rect(x + v.x, y + v.y, width, height);
    }

    /** Returns the center of the Rect */
    public Vector2f getCenter() {
        return new Vector2f(x + width / 2, y + height / 2);
    }

    /** Returns if the specified rectangle intersects with this one. Adjacency does not count. */
    public boolean intersects(Rect other) {
        if ((x >= (other.x + other.width)) || ((x + width) <= other.x)) {
            return false;
        }
        if ((y >= (other.y + other.height)) || ((y + height) <= other.y)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the side of the collision of specified rect with this rectangle. Adjacency does not count.<br>
     * Will return a single side or two adjacent sides indicating a corner unless:
     * <ul>
     * <li>Two rectangles form a cross, where it will return the two opposite sides the other rectangle passes through.
     * <li>One rectangle is inside the other, where it will return all sides
     * <li>Rectangles do not collide, where it will return no sides
     * </ul>
     */
    public EnumSet<Side> getCollision(Rect other) {
        if (!intersects(other)) {
            return EnumSet.noneOf(Side.class);
        }
        int bits = 0;
        if (x < other.x) {
            bits += 8;
        }
        if (x + width > other.x + other.width) {
            bits += 4;
        }
        if (y < other.y) {
            bits += 2;
        }
        if (y + height > other.y + other.height) {
            bits += 1;
        }
        // BIT ORDER: LEFT RIGHT BOTTOM TOP
        switch (bits) {
        case 0: // 0000
        case 15: // 1111
            return EnumSet.allOf(Side.class);
        case 8: // 1000
        case 11: // 1011
            return EnumSet.of(Side.RIGHT);
        case 4: // 0100
        case 7: // 0111
            return EnumSet.of(Side.LEFT);
        case 2: // 0010
        case 14: // 1110
            return EnumSet.of(Side.BOTTOM);
        case 1: // 0001
        case 13: // 1101
            return EnumSet.of(Side.TOP);
        case 10: // 1010
            return EnumSet.of(Side.RIGHT, Side.BOTTOM);
        case 9: // 1001
            return EnumSet.of(Side.RIGHT, Side.TOP);
        case 6: // 0110
            return EnumSet.of(Side.LEFT, Side.BOTTOM);
        case 5: // 0101
            return EnumSet.of(Side.LEFT, Side.TOP);
        case 12: // 1100
            return EnumSet.of(Side.TOP, Side.BOTTOM);
        case 3: // 0011
            return EnumSet.of(Side.LEFT, Side.RIGHT);
        default:
            throw new RuntimeException("Could not determine " + "intersection between " + this + " and " + other);
        }

    }

    public float distanceTo(Rect other) {
        float xd = x - other.x;
        float yd = y - other.y;
        return (float) Math.sqrt(xd * xd - yd * yd);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Vector2f getPosition() {
        return new Vector2f(x, y);
    }

    /**
     * Translate rect to a different origin.
     * 
     * @param x
     * @param y
     * @return
     */
    public Rect translateTo(float x, float y) {
        return new Rect(x, y, width, height);
    }

    /**
     * Translate rect so that the given side is at the given coordinate.
     * 
     * @param side
     * @param pos
     * @return
     */
    public Rect translateSideTo(Side side, float pos) {
        switch (side) {
        case TOP:
        case BOTTOM:
            return translate(0, pos - getSide(side));
        case LEFT:
        case RIGHT:
            return translate(pos - getSide(side), 0);
        default:
            throw new RuntimeException("Bad side");
        }
    }

    /**
     * Get the coordinate of a given side.
     * 
     * @param side
     * @return
     */
    public float getSide(Side side) {
        switch (side) {
        case TOP:
            return y;
        case LEFT:
            return x;
        case BOTTOM:
            return y + height;
        case RIGHT:
            return x + width;
        default:
            throw new RuntimeException("Bad side");
        }
    }

    /**
     * Grow or shrink a rect so that the given side is at the given coordinate.
     * 
     * @param side
     * @param pos
     * @return
     */
    public Rect setSide(Side side, float pos) {
        System.out.println("setSide: " + this + "\n\t" + side + ": " + pos);
        switch (side) {
        case TOP:
            if (height + y - pos < 0)
                return null;
            return new Rect(x, pos, width, height + y - pos);
        case LEFT:
            if (width + x - pos < 0)
                return null;
            return new Rect(pos, y, width + x - pos, height);
        case BOTTOM:
            if (pos - y < 0)
                return null;
            return new Rect(x, y, width, pos - y);
        case RIGHT:
            if (pos - x < 0)
                return null;
            return new Rect(x, y, pos - x, height);
        default:
            throw new RuntimeException("Bad side");
        }
    }

    /**
     * Return true if this is on the "inside" side of the extension of the given side on other.
     * 
     * @param side
     * @param other
     * @return
     */
    private boolean isInsideSide(Side side, Rect other) {
        switch (side) {
        case TOP:
        case LEFT:
            return other.getSide(side) < getSide(side);
        case BOTTOM:
        case RIGHT:
            return other.getSide(side) > getSide(side);
        default:
            throw new RuntimeException("Bad side");
        }
    }

    /**
     * Translate this the smallest distance such that it is inside other.
     * 
     * @param other
     * @return
     */
    public Rect translateInto(Rect other) {
        if (width > other.getWidth() || height > other.getHeight()) {
            return null;
        }
        Rect result = this;
        for (Side s : Side.values()) {
            if (!isInsideSide(s, other)) {
                result = result.translateSideTo(s, other.getSide(s));
            }
        }
        return result;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public float getMaxX() {
        return x + width;
    }

    public float getMaxY() {
        return y + height;
    }

    public Shape toShape() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(height);
        result = prime * result + Float.floatToIntBits(width);
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Rect other = (Rect) obj;
        float diff = Math.abs(height - other.height);
        if (diff > EPS && diff / height > EPS)
            return false;
        diff = Math.abs(width - other.width);
        if (diff > EPS && diff / width > EPS)
            return false;
        diff = Math.abs(x - other.x);
        if (diff > EPS && diff / x > EPS)
            return false;
        diff = Math.abs(y - other.y);
        if (diff > EPS && diff / y > EPS)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Rect [x=" + x + ", y=" + y + ", h=" + height + ", w=" + width + "]";
    }

}
