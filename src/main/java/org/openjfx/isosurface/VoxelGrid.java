package org.openjfx.isosurface;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.BoundingBox;
import org.openjfx.isosurface.sdf.SdfShape;
import org.openjfx.isosurface.util.Float3;
import org.openjfx.isosurface.util.Int3;

/**
 * Stores a 3D scalar field from which surface meshes can be extracted with an {@code SdfMeshBuilder}.
 */
public final class VoxelGrid {
    private final static float DEFAULT_VOXEL_SIZE = 0.1f;

    private final FloatProperty voxelSize;

    private float posX;
    private float posY;
    private float posZ;
    private int width;
    private int height;
    private int depth;
    private float[] voxels;

    /**
     * Creates a new empty {@code VoxelGrid}.
     */
    public VoxelGrid() {
        this.posX = 0;
        this.posY = 0;
        this.posZ = 0;
        this.width = 0;
        this.height = 0;
        this.depth = 0;
        this.voxels = new float[0];
        this.voxelSize = new SimpleFloatProperty(DEFAULT_VOXEL_SIZE);
    }

    /**
     * Returns the number of voxels contained by this voxel grid along the x-axis.
     *
     * @return the number of voxels along the x-axis
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Returns the number of voxels contained by this voxel grid along the y-axis.
     *
     * @return the number of voxels along the y-axis
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Returns the number of voxels contained by this voxel grid along the z-axis.
     *
     * @return the number of voxels along the z-axis
     */
    public int getDepth() {
        return this.depth;
    }

    /**
     * Returns the actual size of each voxel in world space.
     *
     * @return the size of each voxel
     */
    public float getVoxelSize() {
        return voxelSize.get();
    }

    /**
     * Returns the property defining the size of each voxel in world space
     *
     * @return the property defining the size of each voxel
     */
    public FloatProperty voxelSizeProperty() {
        return voxelSize;
    }

    /**
     * Returns true if the specified voxel coordinate falls within the bounds of this voxel grid.
     *
     * @param x the x coordinate of the voxel
     * @param y the y coordinate of the voxel
     * @param z the z coordinate of the voxel
     * @return true if the coordinates are within the bounds of this voxel grid
     */
    private boolean coordsInBounds(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < width && y < height && z < depth;
    }

    /**
     * Calculates the index within internal 1-dimensional array of
     * voxels that corresponds to the specified 3D voxel coordinate.
     * Note that no bounds checking is done here.
     *
     * @param x the x coordinate of the voxel
     * @param y the y coordinate of the voxel
     * @param z the z coordinate of the voxel
     * @return the index of the coordinate within the internal voxel array
     */
    private int coordsToIndex(int x, int y, int z) {
        return (z * width * height) + (y * width) + x;
    }

    /**
     * Returns the value stored at the specified voxel coordinate,
     * or a large default value if the coordinate is out of bounds.
     *
     * @param x the x coordinate of the voxel
     * @param y the y coordinate of the voxel
     * @param z the z coordinate of the voxel
     * @return the value stored at the specified coordinate, or a large default value if the coordinate is out of bounds
     */
    public float getVoxel(int x, int y, int z) {
        if (coordsInBounds(x, y, z)) {
            return voxels[coordsToIndex(x, y, z)];
        } else {
            return 10_000.0f;
        }
    }

    /**
     * Returns the value stored at the specified voxel coordinate,
     * or a large default value if the coordinate is out of bounds.
     *
     * @param coords the coordinates of the voxel
     * @return the value stored at the specified coordinate, or a large default value if the coordinate is out of bounds
     */
    public float getVoxel(Int3 coords) {
        return getVoxel(coords.x(), coords.y(), coords.z());
    }

    /**
     * Sets the value of the voxel at the specified voxel coordinate.
     * Note that no bounds checking is done, so errors may occur if
     * the voxel coordinate is out of bounds.
     *
     * @param x     the x coordinate of the voxel
     * @param y     the y coordinate of the voxel
     * @param z     the z coordinate of the voxel
     * @param value the value to store at the specified voxel coordinate
     */
    private void setVoxel(int x, int y, int z, float value) {
        voxels[coordsToIndex(x, y, z)] = value;
    }

    /**
     * Gets the world-space position of the minimum corner (smallest x, y, and z) of
     * the voxel at the specified coordinate, as if each voxel is a cube in a 3D grid.
     * Note no bounds checking is done, so the returned position may be outside the
     * bounds of any voxels within the voxel grid.
     *
     * @param x the x coordinate of the voxel
     * @param y the y coordinate of the voxel
     * @param z the z coordinate of the voxel
     * @return the world-space position of the minimum corner of the voxel at the specified coordinate
     */
    public Float3 getVoxelCornerPos(int x, int y, int z) {
        final float voxelSize = getVoxelSize();

        return new Float3(
                (float) x * voxelSize + posX,
                (float) y * voxelSize + posY,
                (float) z * voxelSize + posZ
        );
    }

    /**
     * Gets the world-space position of the center of the voxel at the specified coordinate,
     * as if each voxel is a cube in a 3D grid. Note no bounds checking is done, so the
     * returned position may be outside the bounds of any voxels within the voxel grid.
     *
     * @param x the x coordinate of the voxel
     * @param y the y coordinate of the voxel
     * @param z the z coordinate of the voxel
     * @return the world-space position of the center of the voxel at the specified coordinate
     */
    public Float3 getVoxelCenterPos(int x, int y, int z) {
        final float halfVoxelSize = getVoxelSize() * 0.5f;
        return getVoxelCornerPos(x, y, z).add(new Float3(halfVoxelSize, halfVoxelSize, halfVoxelSize));
    }

    /**
     * Gets the world-space position of the center of the voxel at the specified coordinate,
     * as if each voxel is a cube in a 3D grid. Note no bounds checking is done, so the
     * returned position may be outside the bounds of any voxels within the voxel grid.
     *
     * @param coords the coordinates of the voxel
     * @return the world-space position of the center of the voxel at the specified coordinate
     */
    public Float3 getVoxelCenterPos(Int3 coords) {
        return getVoxelCenterPos(coords.x(), coords.y(), coords.z());
    }

    /**
     * Returns the nearest power of 2 to the specified integer.
     *
     * @param x the input integer
     * @return the nearest power of 2 to the input integer
     */
    private int nearestPow2(int x) {
        // ported from https://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
        if (x == 0) {
            return 1;
        } else {
            x--;
            x |= x >> 1;
            x |= x >> 2;
            x |= x >> 4;
            x |= x >> 8;
            x |= x >> 16;
            x++;

            return x;
        }
    }

    /**
     * Alters the position and number of voxels of this voxel grid along
     * each dimension to completely contain a specified {@code SdfShape}.
     *
     * @param shape the shape to fit this voxel grid to
     */
    public void fitToShape(SdfShape shape) {
        final float voxelSize = getVoxelSize();
        final float voxelSizeReciprocal = 1.0f / voxelSize;
        final BoundingBox shapeBounds = shape.getWorldBounds();

        posX = ((float) Math.floor(shapeBounds.getMinX() * voxelSizeReciprocal) - 1.0f) * voxelSize;
        posY = ((float) Math.floor(shapeBounds.getMinY() * voxelSizeReciprocal) - 1.0f) * voxelSize;
        posZ = ((float) Math.floor(shapeBounds.getMinZ() * voxelSizeReciprocal) - 1.0f) * voxelSize;

        width = (int) Math.ceil(shapeBounds.getWidth() * voxelSizeReciprocal) + 2;
        height = (int) Math.ceil(shapeBounds.getHeight() * voxelSizeReciprocal) + 2;
        depth = (int) Math.ceil(shapeBounds.getDepth() * voxelSizeReciprocal) + 2;

        final int numVoxels = width * height * depth;

        // grow voxel array if necessary
        if (numVoxels > voxels.length) {
            voxels = new float[nearestPow2(numVoxels)];
        }
    }

    /**
     * Stores a discretized representation of the specified {@code SdfShape} within this voxel grid.
     *
     * @param shape the shape to store within this voxel grid
     */
    public void voxelizeShape(SdfShape shape) {
        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    final Float3 voxelCenterPos = getVoxelCenterPos(x, y, z);
                    final float distance = (float) shape.getWorldDistance(voxelCenterPos.toPoint3D());

                    setVoxel(x, y, z, distance);
                }
            }
        }
    }
}
