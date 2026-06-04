package org.openjfx.isosurface.model.sdf;

import javafx.beans.property.DoubleProperty;

/**
 * Stores shape parameter metadata for use in the UI.
 * <p>
 * Storing and providing metadata in this way allows the parameter UI controls to be
 * procedurally generated instead of explicitly defined, simplifying changes to the UI.
 *
 * @param name         the parameter's human-readable name
 * @param value        the parameter's bindable value
 * @param defaultValue the parameter's default value
 */
public record ShapeParameter(
        String name,
        DoubleProperty value,
        double defaultValue
) {
}
