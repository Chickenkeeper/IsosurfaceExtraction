package org.openjfx.isosurface.sdf;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;

/**
 * A 3D box shape represented as a signed distance field.
 */
public final class Box extends SdfShape {
    public static final double DEFAULT_WIDTH = 2.0;
    public static final double DEFAULT_HEIGHT = 2.0;
    public static final double DEFAULT_DEPTH = 2.0;

    private final DoubleProperty width;
    private final DoubleProperty height;
    private final DoubleProperty depth;

    /**
     * Creates a new {@code Box} instance with specified dimensions.
     *
     * @param width  the width of the box
     * @param height the height of the box
     * @param depth  the depth of the box
     */
    public Box(double width, double height, double depth) {
        super();
        this.width = new SimpleDoubleProperty(width);
        this.height = new SimpleDoubleProperty(height);
        this.depth = new SimpleDoubleProperty(depth);
    }

    /**
     * Creates a new {@code Box} instance with default dimensions.
     */
    public Box() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_DEPTH);
    }

    /**
     * Returns the property defining the width of the box.
     *
     * @return the width property of the box
     */
    public DoubleProperty widthProperty() {
        return width;
    }

    /**
     * Returns the property defining the height of the box.
     *
     * @return the height property of the box
     */
    public DoubleProperty heightProperty() {
        return height;
    }

    /**
     * Returns the property defining the height of the box.
     *
     * @return the height property of the box
     */
    public DoubleProperty depthProperty() {
        return depth;
    }

    @Override
    public BoundingBox getLocalBounds() {
        final double widthValue = width.get();
        final double heightValue = height.get();
        final double depthValue = depth.get();

        return new BoundingBox(
                -widthValue * 0.5,
                -heightValue * 0.5,
                -depthValue * 0.5,
                widthValue,
                heightValue,
                depthValue
        );
    }

    @Override
    public double getLocalDistance(Point3D point) {
        // ported from https://iquilezles.org/articles/distfunctions/
        final double widthValue = width.get() * 0.5;
        final double heightValue = height.get() * 0.5;
        final double depthValue = depth.get() * 0.5;

        final double pX = point.getX();
        final double pY = point.getY();
        final double pZ = point.getZ();

        final double qX = Math.abs(pX) - widthValue;
        final double qY = Math.abs(pY) - heightValue;
        final double qZ = Math.abs(pZ) - depthValue;

        final double qClampX = Math.max(qX, 0.0);
        final double qClampY = Math.max(qY, 0.0);
        final double qClampZ = Math.max(qZ, 0.0);

        final double qClampLen = Math.sqrt((qClampX * qClampX) + (qClampY * qClampY)) + (qClampZ * qClampZ);
        final double qMaxElem = Math.max(qX, Math.max(qY, qZ));

        return qClampLen + Math.min(qMaxElem, 0.0);
    }

    @Override
    public String toString() {
        return "Box";
    }
}
