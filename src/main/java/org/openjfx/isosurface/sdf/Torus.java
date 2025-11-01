package org.openjfx.isosurface.sdf;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;

/**
 * A 3D torus shape represented as a signed distance field.
 */
public final class Torus extends SdfShape {
    public static final double DEFAULT_MAJOR_RADIUS = 0.7;
    public static final double DEFAULT_MINOR_RADIUS = 0.3;

    private final DoubleProperty majorRadius;
    private final DoubleProperty minorRadius;

    /**
     * Creates a new {@code Torus} instance with a given major and minor radius.
     *
     * @param majorRadius the major radius of the torus
     * @param minorRadius the minor radius of the torus
     */
    public Torus(double majorRadius, double minorRadius) {
        super();
        this.majorRadius = new SimpleDoubleProperty(majorRadius);
        this.minorRadius = new SimpleDoubleProperty(minorRadius);
    }

    /**
     * Creates a new {@code Torus} instance with a default ring and pipe radius.
     */
    public Torus() {
        this(DEFAULT_MAJOR_RADIUS, DEFAULT_MINOR_RADIUS);
    }

    /**
     * Returns the property defining the major radius of the torus.
     *
     * @return the major radius property of the torus
     */
    public DoubleProperty majorRadiusProperty() {
        return majorRadius;
    }

    /**
     * Returns the property defining the minor radius of the torus.
     *
     * @return the minor radius property of the torus
     */
    public DoubleProperty minorRadiusProperty() {
        return minorRadius;
    }

    @Override
    public BoundingBox getLocalBounds() {
        final double majorRadiusValue = majorRadius.get();
        final double minorRadiusValue = minorRadius.get();

        final double halfWidth = majorRadiusValue + minorRadiusValue;
        final double halfHeight = minorRadiusValue;
        final double width = halfWidth * 2.0;
        final double height = halfHeight * 2.0;

        return new BoundingBox(
                -halfWidth,
                -halfHeight,
                -halfWidth,
                width,
                height,
                width
        );
    }

    @Override
    // ported from https://iquilezles.org/articles/distfunctions/
    public double getLocalDistance(Point3D point) {
        final double majorRadiusValue = majorRadius.get();
        final double minorRadiusValue = minorRadius.get();

        final double pX = point.getX();
        final double pY = point.getY();
        final double pZ = point.getZ();
        final double q = Math.sqrt((pX * pX) + (pZ * pZ)) - majorRadiusValue;

        return Math.sqrt((q * q) + (pY * pY)) - minorRadiusValue;
    }

    @Override
    public String toString() {
        return "Torus";
    }
}
