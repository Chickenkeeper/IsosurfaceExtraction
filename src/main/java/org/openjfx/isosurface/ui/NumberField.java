package org.openjfx.isosurface.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.converter.NumberStringConverter;

/**
 * A text input field that allows a user to enter and retrieve a number, displayed with a specified format.
 * The value can be changed by dragging the value or by clicking the field and typing it in like a {@code TextField}.
 * If a value cannot be parsed the number field will revert to the previously stored value.
 */
public final class NumberField extends TextField {
    private static final double DEFAULT_MIN = -Double.MAX_VALUE;
    private static final double DEFAULT_MAX = Double.MAX_VALUE;
    private static final double DEFAULT_VALUE = 0.0;
    private static final double DEFAULT_INCREMENT = 0.01;
    private static final String DEFAULT_PATTERN = "0.0####";

    private final DoubleProperty value;
    private final NumberStringConverter converter;

    private double prevValue;
    private double mouseAnchor;
    private boolean wasDragged;
    private boolean smallIncrement;

    /**
     * Creates a new {@code NumberField} node from a specified minimum,
     * maximum, starting and increment value, and a format pattern.
     *
     * @param min           the minimum value
     * @param max           the maximum value
     * @param value         the starting value
     * @param increment     the amount to change the value while dragging
     * @param formatPattern the format of the displayed value
     */
    public NumberField(double min, double max, double value, double increment, String formatPattern) {
        super();
        this.value = new SimpleDoubleProperty(value);
        this.converter = new NumberStringConverter(formatPattern);
        this.prevValue = 0.0;
        this.mouseAnchor = 0.0;
        this.wasDragged = false;
        this.smallIncrement = false;

        this.value.addListener((observable, oldValue, newValue) -> setTextFromValue());

        setOnMouseDragged(e -> {
            if (!isEditable()) {
                final double inc = smallIncrement ? increment * 0.1 : increment; // use a smaller increment if shift is held
                double newValue = prevValue + (e.getX() - mouseAnchor) * inc;
                newValue = Math.clamp(newValue, min, max);

                // don't fire events if the numerical value hasn't changed
                if (newValue != this.value.get()) {
                    this.value.set(newValue);
                    setTextFromValue();
                    wasDragged = true;
                }

                deselect(); // prevent stray selections while dragging
            }
        });
        setOnMousePressed(e -> {
            if (!isEditable()) {
                // prepare for dragging mode
                prevValue = this.value.get();
                mouseAnchor = e.getX();
                wasDragged = false;
                deselect();
            }
        });
        setOnMouseReleased(e -> {
            if (!isEditable() && !wasDragged) {
                // enter editing mode
                setTextFromValue();
                setEditable(true);
                selectAll();
                setCursor(Cursor.TEXT);
            }
        });
        setOnMouseEntered(e -> setCursor(isEditable() ? Cursor.TEXT : Cursor.H_RESIZE));
        setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
        setOnAction(e -> setValueFromText()); // commit an entered value when enter(?) is pressed
        setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.SHIFT)) {
                smallIncrement = true;
            }
        });
        setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.SHIFT)) {
                smallIncrement = false;
            }
        });

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) setValueFromText(); // commit an entered value if focus is lost
        });

        setAlignment(Pos.CENTER);
        setEditable(false);
        setTextFromValue();
    }

    /**
     * Creates a new {@code NumberField} node from a specified minimum, maximum
     * and starting value, with a default increment and format pattern.
     *
     * @param min   the minimum value
     * @param max   the maximum value
     * @param value the starting value
     */
    public NumberField(double min, double max, double value) {
        this(min, max, value, DEFAULT_INCREMENT, DEFAULT_PATTERN);
    }

    /**
     * Creates a new {@code NumberField} node with default parameters.
     */
    public NumberField() {
        this(DEFAULT_MIN, DEFAULT_MAX, DEFAULT_VALUE, DEFAULT_INCREMENT, DEFAULT_PATTERN);
    }

    /**
     * Returns the property containing the numerical value of this number field.
     *
     * @return the numerical value of this number field
     */
    public DoubleProperty valueProperty() {
        return value;
    }

    /**
     * Sets the displayed text of this number field from its numerical value.
     */
    private void setTextFromValue() {
        final String valueString = converter.toString(value.get());

        setText(valueString);
    }

    /**
     * Sets the stored numerical value of this number field from its displayed text.
     * If the text cannot be parsed it will fall back to the current numerical value.
     */
    private void setValueFromText() {
        try {
            value.setValue(Double.valueOf(getText()));
        } catch (NumberFormatException e) {
            setTextFromValue();
        }

        // exit editing mode
        setEditable(false);
        deselect();
        setCursor(Cursor.H_RESIZE);
    }
}
