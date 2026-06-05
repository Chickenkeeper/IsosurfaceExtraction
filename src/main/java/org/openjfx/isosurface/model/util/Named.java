package org.openjfx.isosurface.model.util;

/**
 * Provides a method for giving a class a human-readable name for display in the UI.
 */
public interface Named {
    /**
     * Gets the name of the implementing class in a human-readable form.
     *
     * @return the human-readable name of the implementing class
     */
    String getDisplayString();
}
