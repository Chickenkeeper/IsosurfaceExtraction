package org.openjfx.isosurface.model.util;

import javafx.beans.property.DoubleProperty;

/**
 * Stores parameter metadata for use in the UI.
 * <p>
 * Storing and providing metadata in this way allows parameter UI controls to be
 * procedurally generated instead of explicitly defined, simplifying changes to the UI.
 *
 * @param name         the parameter's human-readable name
 * @param value        the parameter's bindable value
 * @param defaultValue the parameter's default value
 */
public record DoubleParameter(
        String name,
        DoubleProperty value,
        double defaultValue
) {
}
