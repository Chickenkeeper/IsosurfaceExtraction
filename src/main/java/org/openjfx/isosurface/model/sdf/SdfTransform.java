package org.openjfx.isosurface.model.sdf;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import javafx.scene.transform.*;

/**
 * Provides functionality for transforming sdf shapes and their bounding boxes.
 */
public class SdfTransform {
    private final Scale scale;
    private final Rotate rotationX;
    private final Rotate rotationY;
    private final Rotate rotationZ;
    private final Translate translation;

    private Transform localToWorldTransform;
    private Transform worldToLocalTransform;
    private boolean combinedTransformDirty;

    /**
     * Constructs a new {@code SdfTransform} with a rotation and translation of 0 and a scale of 1.
     */
    public SdfTransform() {
        // initialize the separate transforms to their identities
        this.scale = new Scale(1, 1, 1);
        this.rotationX = new Rotate(0, Rotate.X_AXIS);
        this.rotationY = new Rotate(0, Rotate.Y_AXIS);
        this.rotationZ = new Rotate(0, Rotate.Z_AXIS);
        this.translation = new Translate(0, 0, 0);

        // set up listeners so the combined transforms are only recalculated when a separate transform is changed
        scale.xProperty().addListener(_ -> combinedTransformDirty = true);
        scale.yProperty().addListener(_ -> combinedTransformDirty = true);
        scale.zProperty().addListener(_ -> combinedTransformDirty = true);
        rotationX.angleProperty().addListener(_ -> combinedTransformDirty = true);
        rotationY.angleProperty().addListener(_ -> combinedTransformDirty = true);
        rotationZ.angleProperty().addListener(_ -> combinedTransformDirty = true);
        translation.xProperty().addListener(_ -> combinedTransformDirty = true);
        translation.yProperty().addListener(_ -> combinedTransformDirty = true);
        translation.zProperty().addListener(_ -> combinedTransformDirty = true);

        // initialize the combined transforms
        updateCombinedTransforms();
    }

    /**
     * Gets the value of the X-axis scale property.
     *
     * @return the value of the X-axis scale property
     */
    public double getScaleX() {
        return scale.getX();
    }

    /**
     * Gets the scale along the X-axis.
     *
     * @return the scale along the X-axis
     */
    public DoubleProperty scaleXProperty() {
        return scale.xProperty();
    }

    /**
     * Gets the value of the Y-axis scale property.
     *
     * @return the value of the Y-axis scale property
     */
    public double getScaleY() {
        return scale.getY();
    }

    /**
     * Gets the scale along the Y-axis.
     *
     * @return the scale along the Y-axis
     */
    public DoubleProperty scaleYProperty() {
        return scale.yProperty();
    }

    /**
     * Gets the value of the Z-axis scale property.
     *
     * @return the value of the Z-axis scale property
     */
    public double getScaleZ() {
        return scale.getZ();
    }

    /**
     * Gets the scale along the Z-axis.
     *
     * @return the scale along the Z-axis
     */
    public DoubleProperty scaleZProperty() {
        return scale.zProperty();
    }

    /**
     * Gets the value of the X-axis rotation property.
     *
     * @return the value of the X-axis rotation property
     */
    public double getRotationX() {
        return rotationX.getAngle();
    }

    /**
     * Gets the rotation around the X-axis in degrees.
     *
     * @return the rotation around the X-axis in degrees
     */
    public DoubleProperty rotationXProperty() {
        return rotationX.angleProperty();
    }

    /**
     * Gets the value of the Y-axis rotation property.
     *
     * @return the value of the Y-axis rotation property
     */
    public double getRotationY() {
        return rotationY.getAngle();
    }

    /**
     * Gets the rotation around the Y-axis in degrees.
     *
     * @return the rotation around the Y-axis in degrees
     */
    public DoubleProperty rotationYProperty() {
        return rotationY.angleProperty();
    }

    /**
     * Gets the value of the Z-axis rotation property.
     *
     * @return the value of the Z-axis rotation property
     */
    public double getRotationZ() {
        return rotationZ.getAngle();
    }

    /**
     * Gets the rotation around the Z-axis in degrees.
     *
     * @return the rotation around the Z-axis in degrees
     */
    public DoubleProperty rotationZProperty() {
        return rotationZ.angleProperty();
    }

    /**
     * Gets the value of the X-axis translation property.
     *
     * @return the value of the X-axis translation property
     */
    public double getTranslationX() {
        return translation.getX();
    }

    /**
     * Gets the translation along the X-axis.
     *
     * @return the translation along the X-axis
     */
    public DoubleProperty translationXProperty() {
        return translation.xProperty();
    }

    /**
     * Gets the value of the Y-axis translation property.
     *
     * @return the value of the Y-axis translation property
     */
    public double getTranslationY() {
        return translation.getY();
    }

    /**
     * Gets the translation along the Y-axis.
     *
     * @return the translation along the Y-axis
     */
    public DoubleProperty translationYProperty() {
        return translation.yProperty();
    }

    /**
     * Gets the value of the Z-axis translation property.
     *
     * @return the value of the Z-axis translation property
     */
    public double getTranslationZ() {
        return translation.getZ();
    }

    /**
     * Gets the translation along the Z-axis.
     *
     * @return the translation along the Z-axis
     */
    public DoubleProperty translationZProperty() {
        return translation.zProperty();
    }

    /**
     * Gets an array of points representing the corners of a bounding box.
     *
     * @param bounds the bounding box to extract the corners from
     * @return the corners of the bounding box
     */
    private Point3D[] getBoundingBoxCorners(BoundingBox bounds) {
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
     * Recalculates the combined local-to-world transform.
     */
    private void updateLocalToWorldTransform() {
        localToWorldTransform = translation.clone()
                .createConcatenation(rotationZ)
                .createConcatenation(rotationY)
                .createConcatenation(rotationX)
                .createConcatenation(scale);
    }

    /**
     * Recalculates the combined world-to-local transform.
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
     * Recalculates both the world-to-local and local-to-world transforms.
     */
    private void updateCombinedTransforms() {
        updateLocalToWorldTransform();
        updateWorldToLocalTransform();
        combinedTransformDirty = false;
    }

    /**
     * Transforms a point from local space to world space.
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
     * Transforms a point from world space to local space.
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
     * Transforms an axis-aligned bounding box from local space to world space.
     *
     * @param localBounds the axis-aligned local bounding box
     * @return the axis-aligned world-space bounding box
     */
    public BoundingBox localToWorldBounds(BoundingBox localBounds) {
        // initialize the world-space bounds
        double worldMinX = Double.MAX_VALUE;
        double worldMinY = Double.MAX_VALUE;
        double worldMinZ = Double.MAX_VALUE;

        double worldMaxX = Double.MIN_VALUE;
        double worldMaxY = Double.MIN_VALUE;
        double worldMaxZ = Double.MIN_VALUE;

        // loop over each corner of the local bounds, transforming them to
        // world-space and expanding the new world-space bounds to fit them
        for (Point3D localPoint : getBoundingBoxCorners(localBounds)) {
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
}
