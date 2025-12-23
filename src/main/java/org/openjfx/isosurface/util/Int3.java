package org.openjfx.isosurface.util;

/**
 * A group of 3 integers representing a 3D coordinate.
 * Primarily used to help with accessing voxels in a voxel grid.
 *
 * @param x the x coordinate
 * @param y the y coordinate
 * @param z the z coordinate
 */
public record Int3(int x, int y, int z) {
    /**
     * Returns the sum of this coordinate and another.
     *
     * @param other the coordinate to add
     * @return the sum of this coordinate and the other
     */
    public Int3 add(Int3 other) {
        return new Int3(
                x + other.x(),
                y + other.y(),
                z + other.z()
        );
    }
}
