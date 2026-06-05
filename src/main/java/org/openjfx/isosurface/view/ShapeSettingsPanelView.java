package org.openjfx.isosurface.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import org.openjfx.isosurface.model.sdf.SdfShape;
import org.openjfx.isosurface.model.sdf.SdfTransform;
import org.openjfx.isosurface.model.util.DoubleParameter;
import org.openjfx.isosurface.viewmodel.ApplicationViewModel;

/**
 * Displays the shape settings panel of the UI.
 */
public class ShapeSettingsPanelView extends GridPane {
    /**
     * Constructs a new {@code ShapeSettingPanelView} instance and binds it to the provided {@code ApplicationViewModel}.
     *
     * @param applicationViewModel the application view-model to bind this view to
     */
    public ShapeSettingsPanelView(ApplicationViewModel applicationViewModel) {
        super();

        final Label shapeLabel = new Label("Shape");
        final ScrollableComboBox<SdfShape> shapeSelector = new ScrollableComboBox<>();
        shapeSelector.setMaxWidth(Double.MAX_VALUE);
        shapeSelector.setItems(applicationViewModel.getShapes());
        shapeSelector.getSelectionModel().select(applicationViewModel.getShapeSelectedIndex());
        shapeSelector.getSelectionModel().selectedIndexProperty().addListener((_, _, newValue) ->
                applicationViewModel.shapeSelectedIndexProperty().set(newValue.intValue()));
        applicationViewModel.shapeSelectedIndexProperty().addListener((_, _, newValue) ->
                shapeSelector.getSelectionModel().select(newValue.intValue()));

        final Label xLabel = new Label("X");
        final Label yLabel = new Label("Y");
        final Label zLabel = new Label("Z");

        final SdfTransform shapeTransform = applicationViewModel.getShapeTransform();
        final Label translationLabel = new Label("Translation");
        final NumberField shapeTranslationX =
                new NumberField(-Double.MAX_VALUE, Double.MAX_VALUE, shapeTransform.getTranslationX());
        final NumberField shapeTranslationY =
                new NumberField(-Double.MAX_VALUE, Double.MAX_VALUE, shapeTransform.getTranslationY());
        final NumberField shapeTranslationZ =
                new NumberField(-Double.MAX_VALUE, Double.MAX_VALUE, shapeTransform.getTranslationZ());
        shapeTranslationX.valueProperty().bindBidirectional(shapeTransform.translationXProperty());
        shapeTranslationY.valueProperty().bindBidirectional(shapeTransform.translationYProperty());
        shapeTranslationZ.valueProperty().bindBidirectional(shapeTransform.translationZProperty());

        final Label rotationLabel = new Label("Rotation");
        final NumberField shapeRotationX =
                new NumberField(-Double.MAX_VALUE, Double.MAX_VALUE, shapeTransform.getRotationX(), 1.0, "0.0°");
        final NumberField shapeRotationY =
                new NumberField(-Double.MAX_VALUE, Double.MAX_VALUE, shapeTransform.getRotationY(), 1.0, "0.0°");
        final NumberField shapeRotationZ =
                new NumberField(-Double.MAX_VALUE, Double.MAX_VALUE, shapeTransform.getRotationZ(), 1.0, "0.0°");
        shapeRotationX.valueProperty().bindBidirectional(shapeTransform.rotationXProperty());
        shapeRotationY.valueProperty().bindBidirectional(shapeTransform.rotationYProperty());
        shapeRotationZ.valueProperty().bindBidirectional(shapeTransform.rotationZProperty());

        final Label scaleLabel = new Label("Scale");
        final NumberField shapeScaleX =
                new NumberField(0.001, Double.MAX_VALUE, shapeTransform.getScaleX());
        final NumberField shapeScaleY =
                new NumberField(0.001, Double.MAX_VALUE, shapeTransform.getScaleY());
        final NumberField shapeScaleZ =
                new NumberField(0.001, Double.MAX_VALUE, shapeTransform.getScaleZ());
        shapeScaleX.valueProperty().bindBidirectional(shapeTransform.scaleXProperty());
        shapeScaleY.valueProperty().bindBidirectional(shapeTransform.scaleYProperty());
        shapeScaleZ.valueProperty().bindBidirectional(shapeTransform.scaleZProperty());

        final ColumnConstraints col0Constraint = new ColumnConstraints();
        col0Constraint.setPercentWidth(31);
        final ColumnConstraints col1Constraint = new ColumnConstraints();
        col1Constraint.setPercentWidth(23);
        final ColumnConstraints col2Constraint = new ColumnConstraints();
        col2Constraint.setPercentWidth(23);
        final ColumnConstraints col3Constraint = new ColumnConstraints();
        col3Constraint.setPercentWidth(23);

        final ReadOnlyDoubleProperty rowHeight = shapeTranslationX.heightProperty();
        final RowConstraints otherRowConstraint = new RowConstraints();
        otherRowConstraint.prefHeightProperty().bind(rowHeight);
        otherRowConstraint.minHeightProperty().bind(rowHeight);
        otherRowConstraint.maxHeightProperty().bind(rowHeight);

        add(shapeLabel, 0, 0);
        add(shapeSelector, 1, 0, 3, 1);
        getRowConstraints().add(otherRowConstraint);

        // procedurally construct the shape parameter interface
        int rowCounter = 1;
        for (final SdfShape shape : shapeSelector.getItems()) {
            final RowConstraints rowConstraints = new RowConstraints();

            rowConstraints.prefHeightProperty().bind(Bindings.when(
                    shapeSelector.valueProperty().isEqualTo(shape)).then(rowHeight).otherwise(0.0));
            rowConstraints.minHeightProperty().bind(Bindings.when(
                    shapeSelector.valueProperty().isEqualTo(shape)).then(Region.USE_PREF_SIZE).otherwise(0.0));
            rowConstraints.maxHeightProperty().bind(Bindings.when(
                    shapeSelector.valueProperty().isEqualTo(shape)).then(Region.USE_PREF_SIZE).otherwise(0.0));

            for (final DoubleParameter parameter : shape.getParameters()) {
                final Label label = new Label(parameter.name());
                final NumberField field = new NumberField(0.0, Double.MAX_VALUE, parameter.defaultValue());

                label.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(shape));
                field.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(shape));

                parameter.value().bindBidirectional(field.valueProperty());
                add(label, 0, rowCounter);
                add(field, 1, rowCounter, 3, 1);
                getRowConstraints().add(rowConstraints);
                rowCounter++;
            }
        }

        GridPane.setHalignment(xLabel, HPos.CENTER);
        GridPane.setHalignment(yLabel, HPos.CENTER);
        GridPane.setHalignment(zLabel, HPos.CENTER);

        add(xLabel, 1, rowCounter);
        add(yLabel, 2, rowCounter);
        add(zLabel, 3, rowCounter);
        getRowConstraints().add(otherRowConstraint);

        addRow(rowCounter + 1, translationLabel, shapeTranslationX, shapeTranslationY, shapeTranslationZ);
        getRowConstraints().add(otherRowConstraint);

        addRow(rowCounter + 2, rotationLabel, shapeRotationX, shapeRotationY, shapeRotationZ);
        getRowConstraints().add(otherRowConstraint);

        addRow(rowCounter + 3, scaleLabel, shapeScaleX, shapeScaleY, shapeScaleZ);
        getRowConstraints().add(otherRowConstraint);

        getColumnConstraints().addAll(col0Constraint, col1Constraint, col2Constraint, col3Constraint);
        getStyleClass().add("settings-grid");
    }
}
