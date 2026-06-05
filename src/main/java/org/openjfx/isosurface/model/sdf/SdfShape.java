package org.openjfx.isosurface.model.sdf;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import org.openjfx.isosurface.model.util.DoubleParameter;
import org.openjfx.isosurface.model.util.Named;

import java.util.List;

/**
 * The base class for 3D shapes represented by signed distance fields.
 */
public abstract class SdfShape implements Named {
    /**
     * Gets the local-space bounding box of this shape.
     *
     * @return the local-space bounding box of this shape
     */
    public abstract BoundingBox getLocalBounds();

    /**
     * Gets the signed distance from a point to the surface of this shape in local-space.
     *
     * @param point the local-space point to find the distance from
     * @return the distance between this shape and the local-space point
     */
    public abstract double getLocalDistance(Point3D point);

    /**
     * Gets a list of all parameters of this shape.
     *
     * @return the parameters of this shape
     */
    public abstract List<DoubleParameter> getParameters();
}
