package org.openjfx.isosurface.view;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.openjfx.isosurface.viewmodel.ApplicationViewModel;

/**
 * Displays the statistics panel of the UI.
 */
public class StatisticsPanelView extends GridPane {
    /**
     * Constructs a new {@code StatisticsPanelView} instance and binds it to the provided {@code ApplicationViewModel}.
     *
     * @param applicationViewModel the application view-model to bind this view to
     */
    public StatisticsPanelView(ApplicationViewModel applicationViewModel) {
        super();

        final Label degenTriThresholdLabel = new Label("Degenerate Tri Threshold");
        final NumberField degenTriThresholdValue = new NumberField(0.0, 1.0, applicationViewModel.getTriDegenThreshold(), 0.01, "0.#%");
        degenTriThresholdValue.valueProperty().bindBidirectional(applicationViewModel.triDegenThresholdProperty());

        final Label totalTrianglesLabel = new Label("Total triangles");
        final Label totalTrianglesValueLabel = new Label();
        totalTrianglesValueLabel.textProperty().bind(applicationViewModel.triTotalNumProperty().asString());

        final Label degenerateTrianglesLabel = new Label("Degenerate triangles");
        final Label degenerateTrianglesValueLabel = new Label();
        degenerateTrianglesValueLabel.textProperty().bind(applicationViewModel.triDegenNumProperty().asString());

        final Label voxelizationTimeLabel = new Label("Voxelization time");
        final Label voxelizationTimeValueLabel = new Label();
        voxelizationTimeValueLabel.textProperty().bind(applicationViewModel.timeVoxelizationProperty().asString("%.4fms"));

        final Label surfaceGenerationTimeLabel = new Label("Surface generation time");
        final Label surfaceGenerationTimeValueLabel = new Label();
        surfaceGenerationTimeValueLabel.textProperty().bind(applicationViewModel.timeSurfaceGenerationProperty().asString("%.4fms"));

        final ColumnConstraints col0Constraint = new ColumnConstraints();
        col0Constraint.setPercentWidth(70);
        final ColumnConstraints col1Constraint = new ColumnConstraints();
        col1Constraint.setPercentWidth(30);

        final ReadOnlyDoubleProperty rowHeight = degenTriThresholdValue.heightProperty();
        final RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.prefHeightProperty().bind(rowHeight);
        rowConstraint.minHeightProperty().bind(rowHeight);
        rowConstraint.maxHeightProperty().bind(rowHeight);

        addRow(0, degenTriThresholdLabel, degenTriThresholdValue);
        addRow(1, totalTrianglesLabel, totalTrianglesValueLabel);
        addRow(2, degenerateTrianglesLabel, degenerateTrianglesValueLabel);
        addRow(3, voxelizationTimeLabel, voxelizationTimeValueLabel);
        addRow(4, surfaceGenerationTimeLabel, surfaceGenerationTimeValueLabel);

        getColumnConstraints().addAll(col0Constraint, col1Constraint);
        getRowConstraints().addAll(rowConstraint, rowConstraint, rowConstraint, rowConstraint, rowConstraint);
        getStyleClass().add("settings-grid");
    }
}
