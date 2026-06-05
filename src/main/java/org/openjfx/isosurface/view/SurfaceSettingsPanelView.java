package org.openjfx.isosurface.view;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.openjfx.isosurface.model.suface.SdfMeshBuilder;
import org.openjfx.isosurface.model.voxel.VoxelGrid;
import org.openjfx.isosurface.viewmodel.ApplicationViewModel;

/**
 * Displays the surface settings panel of the UI.
 */
public class SurfaceSettingsPanelView extends GridPane {
    /**
     * Constructs a new {@code SurfaceSettingsPanelView} instance and binds it to the provided {@code ApplicationViewModel}.
     *
     * @param applicationViewModel the application view-model to bind this view to
     */
    public SurfaceSettingsPanelView(ApplicationViewModel applicationViewModel) {
        super();

        final Label algorithmLabel = new Label("Algorithm");
        final ScrollableComboBox<SdfMeshBuilder> algorithmSelector = new ScrollableComboBox<>();
        algorithmSelector.setMaxWidth(Double.MAX_VALUE);
        algorithmSelector.setItems(applicationViewModel.getMeshBuilders());
        algorithmSelector.getSelectionModel().select(applicationViewModel.getMeshBuilderSelectedIndex());
        algorithmSelector.getSelectionModel().selectedIndexProperty().addListener((_, _, newValue) ->
                applicationViewModel.meshBuilderSelectedIndexProperty().set(newValue.intValue()));
        applicationViewModel.meshBuilderSelectedIndexProperty().addListener((_, _, newValue) ->
                algorithmSelector.getSelectionModel().select(newValue.intValue()));

        final VoxelGrid voxelGrid = applicationViewModel.getVoxelGrid();
        final Label voxelSizeLabel = new Label("Voxel Size");
        final NumberField voxelSize =
                new NumberField(0.025, 0.25, voxelGrid.getVoxelSize(), 0.005, "0.000");
        voxelSize.valueProperty().bindBidirectional(voxelGrid.voxelSizeProperty());

        final Label isoLevelLabel = new Label("Iso Level");
        final NumberField isoLevel =
                new NumberField(-Double.MAX_VALUE, Double.MAX_VALUE, applicationViewModel.getIsoLevel(), 0.025, "0.000");
        isoLevel.valueProperty().bindBidirectional(applicationViewModel.isoLevelProperty());

        final Label smoothShadingCheckBoxLabel = new Label("Smooth Shading");
        final CheckBox smoothShadingCheckbox = new CheckBox();
        smoothShadingCheckbox.setAllowIndeterminate(false);
        smoothShadingCheckbox.setSelected(applicationViewModel.isUseSmoothShading());
        smoothShadingCheckbox.selectedProperty().bindBidirectional(applicationViewModel.useSmoothShadingProperty());

        final Label wireFrameCheckBoxLabel = new Label("Wireframe");
        final CheckBox wireframeCheckbox = new CheckBox();
        wireframeCheckbox.setAllowIndeterminate(false);
        wireframeCheckbox.setSelected(applicationViewModel.isDrawWireframe());
        wireframeCheckbox.selectedProperty().bindBidirectional(applicationViewModel.drawWireframeProperty());

        final ColumnConstraints col0Constraint = new ColumnConstraints();
        col0Constraint.setPercentWidth(40);
        final ColumnConstraints col1Constraint = new ColumnConstraints();
        col1Constraint.setPercentWidth(60);

        final ReadOnlyDoubleProperty rowHeight = voxelSize.heightProperty();
        final RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.prefHeightProperty().bind(rowHeight);
        rowConstraint.minHeightProperty().bind(rowHeight);
        rowConstraint.maxHeightProperty().bind(rowHeight);

        addRow(0, algorithmLabel, algorithmSelector);
        addRow(1, voxelSizeLabel, voxelSize);
        addRow(2, isoLevelLabel, isoLevel);
        addRow(3, smoothShadingCheckBoxLabel, smoothShadingCheckbox);
        addRow(4, wireFrameCheckBoxLabel, wireframeCheckbox);

        getColumnConstraints().addAll(col0Constraint, col1Constraint);
        getRowConstraints().addAll(rowConstraint, rowConstraint, rowConstraint, rowConstraint, rowConstraint);
        getStyleClass().add("settings-grid");
    }
}
