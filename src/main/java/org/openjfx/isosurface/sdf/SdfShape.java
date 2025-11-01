package org.openjfx.isosurface.sdf;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import javafx.scene.transform.*;

/**
 * The base class for 3D shapes represented by signed distance fields.
 * It provides functionality for transforming the shapes and their bounding boxes.
 */
public abstract class SdfShape {
    private final Scale scale;
    private final Rotate rotationX;
    private final Rotate rotationY;
    private final Rotate rotationZ;
    private final Translate translation;

    private Transform localToWorldTransform;
    private Transform worldToLocalTransform;
    private boolean combinedTransformDirty;

    /**
     * Returns a new {@code SdfShape} with a rotation and translation of 0 and a scale of 1.
     */
    public SdfShape() {
        // initialize the separate transforms to their identities
        this.scale = new Scale(1, 1, 1);
        this.rotationX = new Rotate(0, Rotate.X_AXIS);
        this.rotationY = new Rotate(0, Rotate.Y_AXIS);
        this.rotationZ = new Rotate(0, Rotate.Z_AXIS);
        this.translation = new Translate(0, 0, 0);

        // set up listeners so the combined transforms are only recalculated when a separate transform is changed
        scale.xProperty().addListener(observable -> combinedTransformDirty = true);
        scale.yProperty().addListener(observable -> combinedTransformDirty = true);
        scale.zProperty().addListener(observable -> combinedTransformDirty = true);
        rotationX.angleProperty().addListener(observable -> combinedTransformDirty = true);
        rotationY.angleProperty().addListener(observable -> combinedTransformDirty = true);
        rotationZ.angleProperty().addListener(observable -> combinedTransformDirty = true);
        translation.xProperty().addListener(observable -> combinedTransformDirty = true);
        translation.yProperty().addListener(observable -> combinedTransformDirty = true);
        translation.zProperty().addListener(observable -> combinedTransformDirty = true);

        // initialize the combined transforms
        updateCombinedTransforms();
    }

    /**
     * Returns the property defining the scale of this shape along the x-axis.
     *
     * @return the scale's x property
     */
    public DoubleProperty scaleXProperty() {
        return scale.xProperty();
    }

    /**
     * Returns the property defining the scale of this shape along the y-axis.
     *
     * @return the scale's y property
     */
    public DoubleProperty scaleYProperty() {
        return scale.yProperty();
    }

    /**
     * Returns the property defining the scale of this shape along the z-axis.
     *
     * @return the scale's z property
     */
    public DoubleProperty scaleZProperty() {
        return scale.zProperty();
    }

    /**
     * Returns the property defining the degrees of rotation of this shape around the x-axis.
     *
     * @return the rotation's x angle property
     */
    public DoubleProperty rotationXProperty() {
        return rotationX.angleProperty();
    }

    /**
     * Returns the property defining the degrees of rotation of this shape around the y-axis.
     *
     * @return the rotation's y angle property
     */
    public DoubleProperty rotationYProperty() {
        return rotationY.angleProperty();
    }

    /**
     * Returns the property defining the degrees of rotation of this shape around the z-axis.
     *
     * @return the rotation's z angle property
     */
    public DoubleProperty rotationZProperty() {
        return rotationZ.angleProperty();
    }

    /**
     * Returns the property defining the translation of this shape along the x-axis.
     *
     * @return the translation's x property
     */
    public DoubleProperty translationXProperty() {
        return translation.xProperty();
    }

    /**
     * Returns the property defining the translation of this shape along the y-axis.
     *
     * @return the translation's y property
     */
    public DoubleProperty translationYProperty() {
        return translation.yProperty();
    }

    /**
     * Returns the property defining the translation of this shape along the z-axis.
     *
     * @return the translation's z property
     */
    public DoubleProperty translationZProperty() {
        return translation.zProperty();
    }

    /**
     * Recalculates the combined local-to-world transform for this shape.
     */
    private void updateLocalToWorldTransform() {
        localToWorldTransform = translation.clone()
                .createConcatenation(rotationZ)
                .createConcatenation(rotationY)
                .createConcatenation(rotationX)
                .createConcatenation(scale);
    }

    /**
     * Recalculates the combined world-to-local transform for this shape.
     * Throws a runtime exception if an element of the shape's scale is 0.
     */
    private void updateWorldToLocalTransform() {
        try {
            worldToLocalTransform = scale.createInverse()
                    .createConcatenation(rotationX.createInverse())
                    .createConcatenation(rotationY.createInverse())
                    .createConcatenation(rotationZ.createInverse())
                    .createConcatenation(translation.createInverse());
        } catch (NonInvertibleTransformException e) {
            // should be unreachable as long as no element of scale is 0
            throw new RuntimeException(e);
        }
    }

    /**
     * Recalculates both the world-to-local and local-to-world transforms for this shape.
     */
    private void updateCombinedTransforms() {
        updateLocalToWorldTransform();
        updateWorldToLocalTransform();
        combinedTransformDirty = false;
    }

    /**
     * Returns an array of points representing the corners of a bounding box.
     *
     * @param bounds the bounding box to extract the corners from
     * @return the corners of the bounding box
     */
    private Point3D[] boundingBoxCorners(BoundingBox bounds) {
        final double minX = bounds.getMinX();
        final double minY = bounds.getMinY();
        final double minZ = bounds.getMinZ();

        final double maxX = bounds.getMaxX();
        final double maxY = bounds.getMaxY();
        final double maxZ = bounds.getMaxZ();

        // the order of the corners doesn't matter for what they're used for
        return new Point3D[]{
                new Point3D(minX, minY, minZ),
                new Point3D(maxX, minY, minZ),
                new Point3D(minX, maxY, minZ),
                new Point3D(maxX, maxY, minZ),
                new Point3D(minX, minY, maxZ),
                new Point3D(maxX, minY, maxZ),
                new Point3D(minX, maxY, maxZ),
                new Point3D(maxX, maxY, maxZ),
        };
    }

    /**
     * Transforms an axis-aligned bounding box from this shape's local space to world space.
     *
     * @param localBounds the axis-aligned local bounding box of this shape
     * @return the axis-aligned world-space bounding box
     */
    private BoundingBox localToWorldBounds(BoundingBox localBounds) {
        // initialize the world-space bounds
        double worldMinX = Double.MAX_VALUE;
        double worldMinY = Double.MAX_VALUE;
        double worldMinZ = Double.MAX_VALUE;

        double worldMaxX = Double.MIN_VALUE;
        double worldMaxY = Double.MIN_VALUE;
        double worldMaxZ = Double.MIN_VALUE;

        // loop over each corner of the local bounds, transforming them to
        // world-space and expanding the new world-space bounds to fit them
        for (Point3D localPoint : boundingBoxCorners(localBounds)) {
            final Point3D worldPoint = localToWorldPoint(localPoint);
            final double worldPointX = worldPoint.getX();
            final double worldPointY = worldPoint.getY();
            final double worldPointZ = worldPoint.getZ();

            worldMinX = Math.min(worldMinX, worldPointX);
            worldMinY = Math.min(worldMinY, worldPointY);
            worldMinZ = Math.min(worldMinZ, worldPointZ);

            worldMaxX = Math.max(worldMaxX, worldPointX);
            worldMaxY = Math.max(worldMaxY, worldPointY);
            worldMaxZ = Math.max(worldMaxZ, worldPointZ);
        }

        return new BoundingBox(
                worldMinX,
                worldMinY,
                worldMinZ,
                worldMaxX - worldMinX,
                worldMaxY - worldMinY,
                worldMaxZ - worldMinZ
        );
    }

    /**
     * Transforms a point from this shape's local space to world space.
     *
     * @param point the point to transform
     * @return the transformed point
     */
    public Point3D localToWorldPoint(Point3D point) {
        if (combinedTransformDirty) {
            updateCombinedTransforms();
        }

        return localToWorldTransform.transform(point);
    }

    /**
     * Transforms a point from world space to this shape's local space.
     *
     * @param point the point to transform
     * @return the transformed point
     */
    public Point3D worldToLocalPoint(Point3D point) {
        if (combinedTransformDirty) {
            updateCombinedTransforms();
        }

        return worldToLocalTransform.transform(point);
    }

    /**
     * Returns the local-space bounding box of this shape.
     *
     * @return the local-space bounding box of this shape
     */
    public abstract BoundingBox getLocalBounds();

    /**
     * Returns the world-space bounding box of this shape.
     *
     * @return the world-space bounding box of this shape
     */
    public BoundingBox getWorldBounds() {
        return localToWorldBounds(getLocalBounds());
    }

    /**
     * Returns the signed distance from a point to the surface of this shape in local-space.
     *
     * @param point the local-space point to find the distance from
     * @return the distance between this shape and the local-space point
     */
    public abstract double getLocalDistance(Point3D point);

    public double getWorldDistance(Point3D point) {
        return getLocalDistance(worldToLocalPoint(point));
    }

    @Override
    public abstract String toString();
}
