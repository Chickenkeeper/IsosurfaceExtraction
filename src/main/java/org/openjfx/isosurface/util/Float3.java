package org.openjfx.isosurface.util;

import javafx.geometry.Point3D;

/**
 * A group of 3 floats representing a 3D coordinate.
 * Primarily used to help add points to a mesh.
 *
 * @param x the x coordinate
 * @param y the y coordinate
 * @param z the z coordinate
 */
public record Float3(float x, float y, float z) {
    /**
     * Returns the sum of this coordinate and another.
     *
     * @param other the coordinate to add
     * @return the sum of this coordinate and the other
     */
    public Float3 add(Float3 other) {
        return new Float3(
                x + other.x(),
                y + other.y(),
                z + other.z()
        );
    }

    /**
     * Returns the result of subtracting another coordinate from this one.
     *
     * @param other the coordinate to subtract
     * @return the other coordinate subtracted from this one
     */
    public Float3 sub(Float3 other) {
        return new Float3(
                x - other.x(),
                y - other.y(),
                z - other.z()
        );
    }

    /**
     * Returns the result of multiplying this coordinate by a scalar.
     *
     * @param scalar the scalar to multiply this coordinate with
     * @return this coordinate multiplied by the scalar
     */
    public Float3 mult(float scalar) {
        return new Float3(
                x * scalar,
                y * scalar,
                z * scalar
        );
    }

    /**
     * Linearly interpolates this coordinate towards another.
     *
     * @param target the coordinate to interpolate towards
     * @param t      a value from 0.0 to 1.0 representing the amount to interpolate towards the other point
     * @return the interpolated coordinate
     */
    public Float3 lerp(Float3 target, float t) {
        return new Float3(
                x * (1.0f - t) + target.x() * t,
                y * (1.0f - t) + target.y() * t,
                z * (1.0f - t) + target.z() * t
        );
    }

    /**
     * Returns the dot product of this coordinate and another.
     *
     * @param other the coordinate to perform a dot product with
     * @return the dot product of this coordinate and the other
     */
    public float dot(Float3 other) {
        return x * other.x() + y * other.y() + z * other.z();
    }

    /**
     * Returns the squared distance between this coordinate and another.
     *
     * @param target the coordinate to find the distance to
     * @return the squared distance between this coordinate and the other
     */
    public float distSq(Float3 target) {
        final Float3 v = target.sub(this);
        return v.dot(v);
    }

    /**
     * Converts this {@code Float3} to a {@code Point3D}.
     *
     * @return this {@code Float3} to a {@code Point3D}
     */
    public Point3D toPoint3D() {
        return new Point3D(x, y, z);
    }
}
