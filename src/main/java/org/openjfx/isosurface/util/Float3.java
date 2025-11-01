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
     * Converts this {@code Float3} to a {@code Point3D}.
     *
     * @return this {@code Float3} to a {@code Point3D}
     */
    public Point3D toPoint3D() {
        return new Point3D(x, y, z);
    }
}
