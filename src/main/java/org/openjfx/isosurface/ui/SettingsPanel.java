package org.openjfx.isosurface.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.HPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import org.openjfx.isosurface.sdf.Box;
import org.openjfx.isosurface.sdf.SdfShape;
import org.openjfx.isosurface.sdf.Sphere;
import org.openjfx.isosurface.sdf.Torus;
import org.openjfx.isosurface.suface.Blocky;
import org.openjfx.isosurface.suface.MarchingCubes;
import org.openjfx.isosurface.suface.SdfMeshBuilder;

/**
 * The main settings panel for this application.
 */
public final class SettingsPanel {
    private final Torus torusShape;
    private final Sphere sphereShape;
    private final Box boxShape;

    // shape properties
    private final ComboBox<SdfShape> shapeSelector;
    private final NumberField torusMajorRadius;
    private final NumberField torusMinorRadius;
    private final NumberField sphereRadius;
    private final NumberField boxWidth;
    private final NumberField boxHeight;
    private final NumberField boxDepth;
    private final NumberField shapeTranslationX;
    private final NumberField shapeTranslationY;
    private final NumberField shapeTranslationZ;
    private final NumberField shapeRotationX;
    private final NumberField shapeRotationY;
    private final NumberField shapeRotationZ;
    private final NumberField shapeScaleX;
    private final NumberField shapeScaleY;
    private final NumberField shapeScaleZ;

    // mesh properties
    private final ComboBox<SdfMeshBuilder> algorithmSelector;
    private final NumberField voxelSize;
    private final NumberField isoLevel;
    private final CheckBox smoothShadingCheckbox;
    private final CheckBox wireframeCheckbox;

    // mesh statistics properties
    private final NumberField degenTriThresholdValue;
    private final Label totalTrianglesValueLabel;
    private final Label degenerateTrianglesValueLabel;
    private final Label voxelizationTimeValueLabel;
    private final Label surfaceGenerationTimeValueLabel;

    private final VBox root;

    private Object focused;

    /**
     * Creates a new {@code SettingsPanel}.
     */
    public SettingsPanel() {
        torusShape = new Torus();
        sphereShape = new Sphere();
        boxShape = new Box();

        shapeSelector = new ComboBox<>();
        shapeSelector.getItems().setAll(torusShape, sphereShape, boxShape);
        shapeSelector.setValue(shapeSelector.getItems().getFirst());

        torusMajorRadius = new NumberField(0.0, Double.MAX_VALUE, Torus.DEFAULT_MAJOR_RADIUS);
        torusMajorRadius.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(torusShape));
        torusShape.majorRadiusProperty().bind(torusMajorRadius.valueProperty());

        torusMinorRadius = new NumberField(0.0, Double.MAX_VALUE, Torus.DEFAULT_MINOR_RADIUS);
        torusMinorRadius.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(torusShape));
        torusShape.minorRadiusProperty().bind(torusMinorRadius.valueProperty());

        sphereRadius = new NumberField(0.0, Double.MAX_VALUE, Sphere.DEFAULT_RADIUS);
        sphereRadius.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(sphereShape));
        shapeSelector.prefWidthProperty().bind(sphereRadius.widthProperty());
        sphereShape.radiusProperty().bind(sphereRadius.valueProperty());

        boxWidth = new NumberField(0.0, Double.MAX_VALUE, Box.DEFAULT_WIDTH);
        boxWidth.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(boxShape));
        boxShape.widthProperty().bind(boxWidth.valueProperty());

        boxHeight = new NumberField(0.0, Double.MAX_VALUE, Box.DEFAULT_HEIGHT);
        boxHeight.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(boxShape));
        boxShape.heightProperty().bind(boxHeight.valueProperty());

        boxDepth = new NumberField(0.0, Double.MAX_VALUE, Box.DEFAULT_DEPTH);
        boxDepth.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(boxShape));
        boxShape.depthProperty().bind(boxDepth.valueProperty());

        shapeTranslationX = new NumberField();
        shapeTranslationY = new NumberField();
        shapeTranslationZ = new NumberField();

        shapeRotationX = new NumberField(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 1.0, "0.0°");
        shapeRotationY = new NumberField(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 1.0, "0.0°");
        shapeRotationZ = new NumberField(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 1.0, "0.0°");

        shapeScaleX = new NumberField(0.001, Double.MAX_VALUE, 1.0);
        shapeScaleY = new NumberField(0.001, Double.MAX_VALUE, 1.0);
        shapeScaleZ = new NumberField(0.001, Double.MAX_VALUE, 1.0);

        for (SdfShape shape : shapeSelector.getItems()) {
            shape.translationXProperty().bind(shapeTranslationX.valueProperty());
            shape.translationYProperty().bind(shapeTranslationY.valueProperty());
            shape.translationZProperty().bind(shapeTranslationZ.valueProperty());

            shape.rotationXProperty().bind(shapeRotationX.valueProperty());
            shape.rotationYProperty().bind(shapeRotationY.valueProperty());
            shape.rotationZProperty().bind(shapeRotationZ.valueProperty());

            shape.scaleXProperty().bind(shapeScaleX.valueProperty());
            shape.scaleYProperty().bind(shapeScaleY.valueProperty());
            shape.scaleZProperty().bind(shapeScaleZ.valueProperty());
        }

        final Blocky blockyMeshBuilder = new Blocky();
        final MarchingCubes marchingCubesMeshBuilder = new MarchingCubes();

        algorithmSelector = new ComboBox<>();
        algorithmSelector.getItems().setAll(marchingCubesMeshBuilder, blockyMeshBuilder);
        algorithmSelector.setValue(algorithmSelector.getItems().getFirst());

        voxelSize = new NumberField(0.025, 0.25, 0.1, 0.005, "0.000");
        algorithmSelector.maxWidthProperty().bind(voxelSize.widthProperty());
        algorithmSelector.prefWidthProperty().bind(voxelSize.widthProperty());

        isoLevel = new NumberField(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 0.025, "0.000");

        smoothShadingCheckbox = new CheckBox();
        smoothShadingCheckbox.setAllowIndeterminate(false);
        smoothShadingCheckbox.setSelected(true);

        wireframeCheckbox = new CheckBox();
        wireframeCheckbox.setAllowIndeterminate(false);
        wireframeCheckbox.setSelected(true);

        degenTriThresholdValue = new NumberField(0.0, 1.0, 0.05, 0.01, "0.#%");
        degenTriThresholdValue.setPrefWidth(50);

        totalTrianglesValueLabel = new Label();
        degenerateTrianglesValueLabel = new Label();
        voxelizationTimeValueLabel = new Label();
        surfaceGenerationTimeValueLabel = new Label();

        root = new VBox();
        root.setOnMousePressed(e -> {
            if (!(e.getSource().equals(focused))) {
                focused = e.getSource();
                root.requestFocus();
            }
        });

        focused = null;

        addShapeSettingsPane();
        addSurfaceSettingsPane();
        addStatisticsPane();
    }

    /**
     * Adds constraints to a grid to force all its rows to be the same height.
     *
     * @param grid the grid to add constraints to
     */
    private void constrainGridRows(GridPane grid) {
        final double gridRowPercentHeight = 100 / (double) grid.getRowCount();

        for (int i = 0; i < grid.getRowCount(); i++) {
            final RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setPercentHeight(gridRowPercentHeight);
            grid.getRowConstraints().add(rowConstraint);
        }
    }

    /**
     * Adds the shape settings section to this settings panel.
     */
    private void addShapeSettingsPane() {
        final Label shapeLabel = new Label("Shape");

        final Label xLabel = new Label("X");
        final Label yLabel = new Label("Y");
        final Label zLabel = new Label("Z");

        final Label translationLabel = new Label("Translation");
        final Label rotationLabel = new Label("Rotation");
        final Label scaleLabel = new Label("Scale");

        final Label torusRingRadiusLabel = new Label("Ring Radius");
        torusRingRadiusLabel.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(torusShape));

        final Label torusPipeRadiusLabel = new Label("Pipe Radius");
        torusPipeRadiusLabel.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(torusShape));

        final Label sphereRadiusLabel = new Label("Radius");
        sphereRadiusLabel.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(sphereShape));

        final Label boxWidthLabel = new Label("Width");
        boxWidthLabel.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(boxShape));

        final Label boxHeightLabel = new Label("Height");
        boxHeightLabel.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(boxShape));

        final Label boxDepthLabel = new Label("Depth");
        boxDepthLabel.visibleProperty().bind(shapeSelector.valueProperty().isEqualTo(boxShape));

        final ReadOnlyDoubleProperty rowHeight = shapeRotationX.heightProperty();

        final RowConstraints torusRowConstraint = new RowConstraints();
        torusRowConstraint.prefHeightProperty().bind(Bindings.when(shapeSelector.valueProperty().isEqualTo(torusShape)).then(rowHeight).otherwise(0.0));
        torusRowConstraint.minHeightProperty().bind(Bindings.when(shapeSelector.valueProperty().isEqualTo(torusShape)).then(Region.USE_PREF_SIZE).otherwise(0.0));
        torusRowConstraint.maxHeightProperty().bind(Bindings.when(shapeSelector.valueProperty().isEqualTo(torusShape)).then(Region.USE_PREF_SIZE).otherwise(0.0));

        final RowConstraints sphereRowConstraint = new RowConstraints();
        sphereRowConstraint.prefHeightProperty().bind(Bindings.when(shapeSelector.valueProperty().isEqualTo(sphereShape)).then(rowHeight).otherwise(0.0));
        sphereRowConstraint.minHeightProperty().bind(Bindings.when(shapeSelector.valueProperty().isEqualTo(sphereShape)).then(Region.USE_PREF_SIZE).otherwise(0.0));
        sphereRowConstraint.maxHeightProperty().bind(Bindings.when(shapeSelector.valueProperty().isEqualTo(sphereShape)).then(Region.USE_PREF_SIZE).otherwise(0.0));

        final RowConstraints boxRowConstraint = new RowConstraints();
        boxRowConstraint.prefHeightProperty().bind(Bindings.when(shapeSelector.valueProperty().isEqualTo(boxShape)).then(rowHeight).otherwise(0.0));
        boxRowConstraint.minHeightProperty().bind(Bindings.when(shapeSelector.valueProperty().isEqualTo(boxShape)).then(Region.USE_PREF_SIZE).otherwise(0.0));
        boxRowConstraint.maxHeightProperty().bind(Bindings.when(shapeSelector.valueProperty().isEqualTo(boxShape)).then(Region.USE_PREF_SIZE).otherwise(0.0));

        final RowConstraints otherRowConstraint = new RowConstraints();
        otherRowConstraint.prefHeightProperty().bind(rowHeight);
        otherRowConstraint.minHeightProperty().bind(rowHeight);
        otherRowConstraint.maxHeightProperty().bind(rowHeight);

        final GridPane grid = new GridPane();
        grid.getStyleClass().add("settings-grid");

        grid.add(shapeLabel, 0, 0);
        grid.add(shapeSelector, 1, 0, 3, 1);
        grid.getRowConstraints().add(otherRowConstraint);

        grid.add(torusRingRadiusLabel, 0, 1);
        grid.add(torusMajorRadius, 1, 1, 3, 1);
        grid.getRowConstraints().add(torusRowConstraint);

        grid.add(torusPipeRadiusLabel, 0, 2);
        grid.add(torusMinorRadius, 1, 2, 3, 1);
        grid.getRowConstraints().add(torusRowConstraint);

        grid.add(sphereRadiusLabel, 0, 3);
        grid.add(sphereRadius, 1, 3, 3, 1);
        grid.getRowConstraints().add(sphereRowConstraint);

        grid.add(boxWidthLabel, 0, 4);
        grid.add(boxWidth, 1, 4, 3, 1);
        grid.getRowConstraints().add(boxRowConstraint);

        grid.add(boxHeightLabel, 0, 5);
        grid.add(boxHeight, 1, 5, 3, 1);
        grid.getRowConstraints().add(boxRowConstraint);

        grid.add(boxDepthLabel, 0, 6);
        grid.add(boxDepth, 1, 6, 3, 1);
        grid.getRowConstraints().add(boxRowConstraint);

        grid.add(xLabel, 1, 7);
        grid.add(yLabel, 2, 7);
        grid.add(zLabel, 3, 7);
        grid.getRowConstraints().add(otherRowConstraint);

        grid.add(translationLabel, 0, 8);
        grid.add(shapeTranslationX, 1, 8);
        grid.add(shapeTranslationY, 2, 8);
        grid.add(shapeTranslationZ, 3, 8);
        grid.getRowConstraints().add(otherRowConstraint);

        grid.add(rotationLabel, 0, 9);
        grid.add(shapeRotationX, 1, 9);
        grid.add(shapeRotationY, 2, 9);
        grid.add(shapeRotationZ, 3, 9);
        grid.getRowConstraints().add(otherRowConstraint);

        grid.add(scaleLabel, 0, 10);
        grid.add(shapeScaleX, 1, 10);
        grid.add(shapeScaleY, 2, 10);
        grid.add(shapeScaleZ, 3, 10);
        grid.getRowConstraints().add(otherRowConstraint);

        GridPane.setHalignment(xLabel, HPos.CENTER);
        GridPane.setHalignment(yLabel, HPos.CENTER);
        GridPane.setHalignment(zLabel, HPos.CENTER);

        ColumnConstraints col0Constraint = new ColumnConstraints();
        col0Constraint.setPercentWidth(31);
        ColumnConstraints col1Constraint = new ColumnConstraints();
        col1Constraint.setPercentWidth(23);
        ColumnConstraints col2Constraint = new ColumnConstraints();
        col2Constraint.setPercentWidth(23);
        ColumnConstraints col3Constraint = new ColumnConstraints();
        col3Constraint.setPercentWidth(23);

        grid.getColumnConstraints().addAll(col0Constraint, col1Constraint, col2Constraint, col3Constraint);

        root.getChildren().add(new TitledPane("Shape Settings", grid));
    }

    /**
     * Adds the surface settings section to this settings panel.
     */
    private void addSurfaceSettingsPane() {
        final Label algorithmLabel = new Label("Algorithm");
        final Label voxelSizeLabel = new Label("Voxel Size");
        final Label isoLevelLabel = new Label("Iso Level");
        final Label smoothShadingCheckBoxLabel = new Label("Smooth Shading");
        final Label wireFrameCheckBoxLabel = new Label("Wireframe");

        final GridPane grid = new GridPane();
        grid.getStyleClass().add("settings-grid");

        grid.add(algorithmLabel, 0, 0);
        grid.add(algorithmSelector, 1, 0);

        grid.add(voxelSizeLabel, 0, 1);
        grid.add(voxelSize, 1, 1);

        grid.add(isoLevelLabel, 0, 2);
        grid.add(isoLevel, 1, 2);

        grid.add(smoothShadingCheckBoxLabel, 0, 3);
        grid.add(smoothShadingCheckbox, 1, 3);

        grid.add(wireFrameCheckBoxLabel, 0, 4);
        grid.add(wireframeCheckbox, 1, 4);

        GridPane.setHgrow(voxelSize, Priority.ALWAYS);
        GridPane.setHgrow(isoLevel, Priority.ALWAYS);

        ColumnConstraints col0Constraint = new ColumnConstraints();
        col0Constraint.setPercentWidth(40);
        ColumnConstraints col1Constraint = new ColumnConstraints();
        col1Constraint.setPercentWidth(60);

        constrainGridRows(grid);
        grid.getColumnConstraints().addAll(col0Constraint, col1Constraint);

        root.getChildren().add(new TitledPane("Surface Settings", grid));
    }

    /**
     * Adds the statistics section to this settings panel.
     */
    private void addStatisticsPane() {
        final Label degenTriThresholdLabel = new Label("Degen Tri Threshold");
        final Label totalTriangles = new Label("Total triangles");
        final Label degenerateTrianglesLabel = new Label("Degenerate triangles");
        final Label voxelizationTimeLabel = new Label("Voxelization time");
        final Label surfaceGenerationTimeLabel = new Label("Surface generation time");

        final GridPane grid = new GridPane();
        grid.getStyleClass().add("settings-grid");

        grid.add(degenTriThresholdLabel, 0, 0);
        grid.add(degenTriThresholdValue, 1, 0);

        grid.add(totalTriangles, 0, 1);
        grid.add(totalTrianglesValueLabel, 1, 1);

        grid.add(degenerateTrianglesLabel, 0, 2);
        grid.add(degenerateTrianglesValueLabel, 1, 2);

        grid.add(voxelizationTimeLabel, 0, 3);
        grid.add(voxelizationTimeValueLabel, 1, 3);

        grid.add(surfaceGenerationTimeLabel, 0, 4);
        grid.add(surfaceGenerationTimeValueLabel, 1, 4);

        GridPane.setHgrow(degenTriThresholdValue, Priority.ALWAYS);

        ColumnConstraints col0Constraint = new ColumnConstraints();
        col0Constraint.setPercentWidth(70);
        ColumnConstraints col1Constraint = new ColumnConstraints();
        col1Constraint.setPercentWidth(30);

        constrainGridRows(grid);
        grid.getColumnConstraints().addAll(col0Constraint, col1Constraint);

        root.getChildren().add(new TitledPane("Statistics", grid));
    }

    /**
     * Returns the root node of this settings panel.
     *
     * @return the root node of this settings panel
     */
    public VBox getRoot() {
        return root;
    }

    /**
     * Returns the {@code ComboBox} which allows the user to select the current {@code SdfShape}.
     *
     * @return the shape selection combo-box
     */
    public ComboBox<SdfShape> getShapeSelector() {
        return shapeSelector;
    }

    /**
     * Returns the {@code NumberField} which specifies the major radius of the {@code Torus} shape.
     *
     * @return the number field specifying the major radius of the torus shape
     */
    public NumberField getTorusMajorRadius() {
        return torusMajorRadius;
    }

    /**
     * Returns the {@code NumberField} which specifies the minor radius of the {@code Torus} shape.
     *
     * @return the number field specifying the minor radius of the torus shape
     */
    public NumberField getTorusMinorRadius() {
        return torusMinorRadius;
    }

    /**
     * Returns the {@code NumberField} which specifies the radius of the {@code Sphere} shape.
     *
     * @return the number field specifying the radius of the sphere shape
     */
    public NumberField getSphereRadius() {
        return sphereRadius;
    }

    /**
     * Returns the {@code NumberField} which specifies the width of the {@code Box} shape.
     *
     * @return the number field specifying the radius of the box shape
     */
    public NumberField getBoxWidth() {
        return boxWidth;
    }

    /**
     * Returns the {@code NumberField} which specifies the height of the {@code Box} shape.
     *
     * @return the number field specifying the height of the box shape
     */
    public NumberField getBoxHeight() {
        return boxHeight;
    }

    /**
     * Returns the {@code NumberField} which specifies the depth of the {@code Box} shape.
     *
     * @return the number field specifying the depth of the box shape
     */
    public NumberField getBoxDepth() {
        return boxDepth;
    }

    /**
     * Returns the {@code NumberField} which specifies the translation
     * of the currently selected {@code SdfShape} along the x-axis.
     *
     * @return the number field specifying the translation of the currently selected shape along the x-axis
     */
    public NumberField getShapeTranslationX() {
        return shapeTranslationX;
    }

    /**
     * Returns the {@code NumberField} which specifies the translation
     * of the currently selected {@code SdfShape} along the y-axis.
     *
     * @return the number field specifying the translation of the currently selected shape along the y-axis
     */
    public NumberField getShapeTranslationY() {
        return shapeTranslationY;
    }

    /**
     * Returns the {@code NumberField} which specifies the translation
     * of the currently selected {@code SdfShape} along the z-axis.
     *
     * @return the number field specifying the translation of the currently selected shape along the z-axis
     */
    public NumberField getShapeTranslationZ() {
        return shapeTranslationZ;
    }

    /**
     * Returns the {@code NumberField} which specifies the rotation in
     * degrees of the currently selected {@code SdfShape} around the x-axis.
     *
     * @return the number field specifying the rotation in degrees of the currently selected shape around the x-axis
     */
    public NumberField getShapeRotationX() {
        return shapeRotationX;
    }

    /**
     * Returns the {@code NumberField} which specifies the rotation in
     * degrees of the currently selected {@code SdfShape} around the y-axis.
     *
     * @return the number field specifying the rotation in degrees of the currently selected shape around the y-axis
     */
    public NumberField getShapeRotationY() {
        return shapeRotationY;
    }

    /**
     * Returns the {@code NumberField} which specifies the rotation in
     * degrees of the currently selected {@code SdfShape} around the z-axis.
     *
     * @return the number field specifying the rotation in degrees of the currently selected shape around the z-axis
     */
    public NumberField getShapeRotationZ() {
        return shapeRotationZ;
    }

    /**
     * Returns the {@code NumberField} which specifies the scale
     * of the currently selected {@code SdfShape} along the x-axis.
     *
     * @return the number field specifying the scale of the currently selected shape along the x-axis
     */
    public NumberField getShapeScaleX() {
        return shapeScaleX;
    }

    /**
     * Returns the {@code NumberField} which specifies the scale
     * of the currently selected {@code SdfShape} along the y-axis.
     *
     * @return the number field specifying the scale of the currently selected shape along the y-axis
     */
    public NumberField getShapeScaleY() {
        return shapeScaleY;
    }

    /**
     * Returns the {@code NumberField} which specifies the scale
     * of the currently selected {@code SdfShape} along the z-axis.
     *
     * @return the number field specifying the scale of the currently selected shape along the z-axis
     */
    public NumberField getShapeScaleZ() {
        return shapeScaleZ;
    }

    /**
     * Returns the {@code ComboBox} which allows the user to select the current {@code SdfMeshBuilder}.
     *
     * @return the shape selection combo-box
     */
    public ComboBox<SdfMeshBuilder> getAlgorithmSelector() {
        return algorithmSelector;
    }

    /**
     * Returns the {@code NumberField} which specifies the size of a voxel in a {code VoxelGrid}.
     *
     * @return the number field specifying the size of a voxel in a voxel grid
     */
    public NumberField getVoxelSize() {
        return voxelSize;
    }

    /**
     * Returns the {@code NumberField} which specifies the iso level
     * when constructing surface meshes with an {@code SdfMeshBuilder}.
     *
     * @return the number field specifying the iso level of a surface mesh
     */
    public NumberField getIsoLevel() {
        return isoLevel;
    }

    /**
     * Returns the {@code CheckBox} which specifies whether a triangle mesh should be rendered with smooth shading.
     *
     * @return the number field specifying whether a rendered mesh should be shaded smoothly
     */
    public CheckBox getSmoothShadingCheckbox() {
        return smoothShadingCheckbox;
    }

    /**
     * Returns the {@code CheckBox} which specifies whether a triangle mesh should be rendered as a wireframe.
     *
     * @return the number field specifying whether a rendered mesh should be rendered as a wireframe
     */
    public CheckBox getWireframeCheckbox() {
        return wireframeCheckbox;
    }

    /**
     * Returns the {@code NumberField} which specifies the minimum length of
     * the edge of a triangle of a mesh before it is considered degenerate.
     *
     * @return the number field specifying the minimum edge length of a triangle of a mesh before it is considered degenerate
     */
    public NumberField getDegenTriThresholdValue() {
        return degenTriThresholdValue;
    }

    /**
     * Sets the value displayed by the total triangles label in the statistics pane.
     *
     * @param numTris the total number of triangles to display in the statistics pane
     */
    public void setTotalTrianglesValue(int numTris) {
        totalTrianglesValueLabel.setText(String.valueOf(numTris));
    }

    /**
     * Sets the value displayed by the degenerate triangles label in the statistics pane.
     *
     * @param numDegenTris the number of degenerate triangles to display in the statistics pane
     */
    public void setDegenerateTrianglesValue(int numDegenTris) {
        degenerateTrianglesValueLabel.setText(String.valueOf(numDegenTris));
    }

    /**
     * Sets the value displayed by the voxelization time label in the statistics pane.
     *
     * @param timeMs the voxelization duration in milliseconds to display in the statistics pane
     */
    public void setVoxelizationTimeValue(double timeMs) {
        voxelizationTimeValueLabel.setText(String.format("%.4fms", timeMs));
    }

    /**
     * Sets the value displayed by the surface generation time label in the statistics pane.
     *
     * @param timeMs the surface generation duration in milliseconds to display in the statistics pane
     */
    public void setSurfaceGenerationTimeValue(double timeMs) {
        surfaceGenerationTimeValueLabel.setText(String.format("%.4fms", timeMs));
    }
}
