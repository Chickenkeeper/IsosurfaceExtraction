package org.openjfx.isosurface.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

/**
 * A text input field that allows a user to enter and retrieve a number, displayed with a specified format.
 * <p>
 * The value can be changed by clicking and dragging on the field, or by clicking
 * the field and typing in a value as with a {@code TextField}. If an entered
 * value can't be parsed, the number field will revert to its previous value.
 */
public final class NumberField extends TextField {
    // default parameters
    private static final double DEFAULT_MIN = -Double.MAX_VALUE;
    private static final double DEFAULT_MAX = Double.MAX_VALUE;
    private static final double DEFAULT_VALUE = 0.0;
    private static final double DEFAULT_INCREMENT = 0.01;
    private static final String DEFAULT_FORMAT_PATTERN = "0.0####";

    // how much to change the number of increments per step.
    // the number of 'normal' increments stored is multiplied by 10 to allow for smaller
    // increments when holding the shift key down, while still reducing numerical error
    private static final long NUM_INCREMENTS_DELTA_SMALL = 1;
    private static final long NUM_INCREMENTS_DELTA_NORMAL = 10;

    // instance parameters
    private final double min;
    private final double max;
    private final DoubleProperty value;
    private final double increment;
    private final NumberStringConverter formatConverter;

    // variables used for calculating changes
    private long numIncrements;
    private long prevNumIncrements;
    private double inputValue;
    private double mouseAnchorX;
    private double mouseAnchorY;
    private boolean valueUpdatedInternally; // needed for bidirectional binding support

    /**
     * Constructs a new {@code NumberField} node from a specified minimum,
     * maximum, starting and increment value, and a format pattern.
     *
     * @param min           the minimum value
     * @param max           the maximum value
     * @param value         the starting value
     * @param increment     the amount to change the value per step while dragging or scrolling
     * @param formatPattern the format of the displayed value
     */
    public NumberField(double min, double max, double value, double increment, String formatPattern) {
        super();

        this.min = min;
        this.max = max;
        this.value = new SimpleDoubleProperty(value);
        this.increment = increment;
        this.formatConverter = new NumberStringConverter(formatPattern);

        this.numIncrements = 0;
        this.prevNumIncrements = 0;
        this.inputValue = value;
        this.mouseAnchorX = 0.0;
        this.mouseAnchorY = 0.0;
        this.valueUpdatedInternally = false;

        this.value.addListener((_, _, newValue) -> {
            if (!valueUpdatedInternally) {
                numIncrements = 0;
                inputValue = newValue.doubleValue();
            } else {
                valueUpdatedInternally = false;
            }
        });
        focusedProperty().addListener((_, _, newValue) -> {
            if (!newValue) {
                setValueFromText(); // commit an entered value if focus is lost
            }
        });
        setOnAction(_ -> setValueFromText()); // commit an entered value when the enter key's pressed
        setOnMouseDragged(e -> {
            if (!isEditable()) {
                final long mouseDeltaX = (long) (e.getX() - mouseAnchorX);
                final long numIncrementsDelta = e.isShiftDown() ? NUM_INCREMENTS_DELTA_SMALL : NUM_INCREMENTS_DELTA_NORMAL;

                numIncrements = prevNumIncrements + (mouseDeltaX * numIncrementsDelta);
                valueUpdatedInternally = true;
                updateDisplayValue();
                deselect(); // prevent stray selections while dragging
            }
        });
        setOnMouseEntered(_ -> setCursor(isEditable() ? Cursor.TEXT : Cursor.H_RESIZE));
        setOnMouseExited(_ -> setCursor(Cursor.DEFAULT));
        setOnMousePressed(e -> {
            if (!isEditable()) {
                // prepare for dragging mode
                prevNumIncrements = numIncrements;
                mouseAnchorX = e.getX();
                mouseAnchorY = e.getY();
            }
        });
        setOnMouseReleased(e -> {
            if (!isEditable() && e.getX() == mouseAnchorX && e.getY() == mouseAnchorY) {
                // enter editing mode
                setEditable(true);
                selectAll();
                setCursor(Cursor.TEXT);
            }
        });
        setOnScroll(e -> {
            final boolean shiftIsDown = e.isShiftDown();
            final double scrollDelta = shiftIsDown ? e.getDeltaX() : e.getDeltaY(); // scroll axes are swapped when shift is held
            final long numIncrementsDelta = shiftIsDown ? NUM_INCREMENTS_DELTA_SMALL : NUM_INCREMENTS_DELTA_NORMAL;

            numIncrements += scrollDelta > 0 ? numIncrementsDelta : -numIncrementsDelta;
            valueUpdatedInternally = true;
            updateDisplayValue();
        });

        setAlignment(Pos.CENTER);
        setEditable(false);
        setText(formatConverter.toString(value));
    }

    /**
     * Constructs a new {@code NumberField} node from a specified minimum, maximum
     * and starting value, with a default increment and format pattern.
     *
     * @param min   the minimum value
     * @param max   the maximum value
     * @param value the starting value
     */
    public NumberField(double min, double max, double value) {
        this(min, max, value, DEFAULT_INCREMENT, DEFAULT_FORMAT_PATTERN);
    }

    /**
     * Constructs a new {@code NumberField} node with default parameters.
     */
    public NumberField() {
        this(DEFAULT_MIN, DEFAULT_MAX, DEFAULT_VALUE, DEFAULT_INCREMENT, DEFAULT_FORMAT_PATTERN);
    }

    /**
     * Gets the property containing the numerical value of this number field.
     *
     * @return the numerical value of this number field
     */
    public DoubleProperty valueProperty() {
        return value;
    }

    /**
     * Computes and sets the value of this number field.
     */
    private void updateDisplayValue() {
        double newValue = ((double) numIncrements / (double) NUM_INCREMENTS_DELTA_NORMAL * increment) + inputValue;

        // don't trigger events if the numerical value hasn't changed
        if (value.get() == newValue) {
            return;
        }

        if (newValue > max) {
            numIncrements = 0;
            inputValue = max;
            newValue = max;
        } else if (newValue < min) {
            numIncrements = 0;
            inputValue = min;
            newValue = min;
        }

        value.set(newValue);
        setText(formatConverter.toString(value.get()));
    }

    /**
     * Sets the stored numerical value of this number field from its displayed text.
     * If the text cannot be parsed it will fall back to the current numerical value.
     */
    private void setValueFromText() {
        try {
            inputValue = Double.parseDouble(getText());
            numIncrements = 0;
        } catch (NumberFormatException _) {
            // ignore exceptions, just revert the text instead
        } finally {
            updateDisplayValue();

            // exit editing mode
            setEditable(false);
            deselect();
            setCursor(Cursor.H_RESIZE);
        }
    }
}
