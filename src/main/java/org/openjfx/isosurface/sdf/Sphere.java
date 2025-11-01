package org.openjfx.isosurface.sdf;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;

/**
 * A 3D sphere shape represented as a signed distance field.
 */
public final class Sphere extends SdfShape {
    public static final double DEFAULT_RADIUS = 1.0;

    private final DoubleProperty radius;

    /**
     * Creates a new {@code Sphere} instance with a given radius.
     *
     * @param radius the radius of the sphere
     */
    public Sphere(double radius) {
        super();
        this.radius = new SimpleDoubleProperty(radius);
    }

    /**
     * Creates a new {@code Sphere} instance with a default radius.
     */
    public Sphere() {
        this(DEFAULT_RADIUS);
    }

    /**
     * Returns the property defining the radius of the sphere.
     *
     * @return the radius property of the sphere
     */
    public DoubleProperty radiusProperty() {
        return radius;
    }

    @Override
    public BoundingBox getLocalBounds() {
        final double radiusValue = radius.get();
        final double diameter = radiusValue * 2.0;

        return new BoundingBox(
                -radiusValue,
                -radiusValue,
                -radiusValue,
                diameter,
                diameter,
                diameter
        );
    }

    @Override
    public double getLocalDistance(Point3D point) {
        // ported from https://iquilezles.org/articles/distfunctions/
        final double radiusValue = radius.get();

        final double pX = point.getX();
        final double pY = point.getY();
        final double pZ = point.getZ();

        return Math.sqrt((pX * pX) + (pY * pY) + (pZ * pZ)) - radiusValue;
    }

    @Override
    public String toString() {
        return "Sphere";
    }
}
