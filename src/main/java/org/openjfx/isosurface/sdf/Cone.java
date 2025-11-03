package org.openjfx.isosurface.sdf;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;

/**
 * A 3D cone shape represented as a signed distance field.
 */
public final class Cone extends SdfShape {
    public static final double DEFAULT_RADIUS = 1.0;
    public static final double DEFAULT_HEIGHT = 2.0;

    private final DoubleProperty radius;
    private final DoubleProperty height;

    /**
     * Creates a new {@code Cone} instance with a given radius and height.
     *
     * @param radius the radius of the base of the cone
     * @param height the height of the cone
     */
    public Cone(double radius, double height) {
        super();
        this.radius = new SimpleDoubleProperty(radius);
        this.height = new SimpleDoubleProperty(height);
    }

    /**
     * Creates a new {@code Cone} instance with a default radius and height.
     */
    public Cone() {
        this(DEFAULT_RADIUS, DEFAULT_HEIGHT);
    }

    /**
     * Returns the property defining the radius of the base of the cone.
     *
     * @return the radius property of the cone
     */
    public DoubleProperty radiusProperty() {
        return radius;
    }

    /**
     * Returns the property defining the height of the cone.
     *
     * @return the height property of the cone
     */
    public DoubleProperty heightProperty() {
        return height;
    }

    @Override
    public BoundingBox getLocalBounds() {
        final double radiusValue = radius.get();
        final double heightValue = height.get();
        final double diameter = radiusValue * 2.0;
        final double halfHeight = heightValue * 0.5;

        return new BoundingBox(
                -radiusValue,
                -halfHeight,
                -radiusValue,
                diameter,
                heightValue,
                diameter
        );
    }

    @Override
    public double getLocalDistance(Point3D point) {
        // ported from https://iquilezles.org/articles/distfunctions/
        final double heightValue = height.get();

        final double pX = point.getX();
        final double pZ = point.getZ();

        final double qX = radius.get();
        final double qY = -heightValue;

        final double wX = Math.sqrt((pX * pX) + (pZ * pZ));
        final double wY = point.getY() - heightValue * 0.5;

        final double dotWQ = (wX * qX) + (wY * qY);
        final double dotQQ = (qX * qX) + (qY * qY);

        final double clamp1 = Math.clamp(dotWQ / dotQQ, 0.0, 1.0);
        final double clamp2 = Math.clamp(wX / qX, 0.0, 1.0);

        final double aX = wX - (qX * clamp1);
        final double aY = wY - (qY * clamp1);

        final double bX = wX - (qX * clamp2);
        final double bY = wY - qY;

        final double k = Math.signum(qY);

        final double dotAA = (aX * aX) + (aY * aY);
        final double dotBB = (bX * bX) + (bY * bY);

        final double d = Math.min(dotAA, dotBB);
        final double s = Math.max(k * (wX * qY - wY * qX), k * (wY - qY));

        return Math.sqrt(d) * Math.signum(s);
    }

    @Override
    public String toString() {
        return "Cone";
    }
}
