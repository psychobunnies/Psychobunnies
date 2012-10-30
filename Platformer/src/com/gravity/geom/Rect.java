package com.gravity.geom;

import java.util.EnumSet;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import com.google.common.base.Preconditions;

/**
 * A rectangle with sides parallel to axes.
 * Custom built, because the one in slick has bugs and performance issues.
 * 
 * @author xiao
 * 
 */
public class Rect {
    private static final float EPS = 1e-6f;
    
    private final float x, y;
    private final float height, width;
    
    /**
     * Enum representing the corners of the rectangle.
     * <ul>
     * <li>BOTLEFT is (x, y)
     * <li>TOPRIGHT is (x + width, y + height)
     * </ul>
     * 
     * @author xiao
     */
    public static enum Corner {
        TOPLEFT, TOPRIGHT, BOTLEFT, BOTRIGHT;
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
    }
    
    public Rect(Shape rect) {
        x = rect.getMinX();
        y = rect.getMinY();
        width = rect.getWidth();
        height = rect.getHeight();
    }
    
    public Rect(float x, float y, float width, float height) {
        Preconditions.checkArgument(height > 0, "Height must be positive");
        Preconditions.checkArgument(width > 0, "Width must be positive");
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
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public Vector2f getPosition() {
        return new Vector2f(x, y);
    }
    
    public Rect setPosition(float x, float y) {
        return new Rect(x, y, width, height);
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
        if (Math.abs(height - other.height) > EPS)
            return false;
        if (Math.abs(width - other.width) > EPS)
            return false;
        if (Math.abs(x - other.x) > EPS)
            return false;
        if (Math.abs(y - other.y) > EPS)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "Rect [x=" + x + ", y=" + y + ", h=" + height + ", w=" + width + "]";
    }
    
}
