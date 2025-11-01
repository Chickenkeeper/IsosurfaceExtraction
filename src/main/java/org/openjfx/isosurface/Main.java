package org.openjfx.isosurface;

import javafx.application.Application;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.geometry.Point3D;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
import org.openjfx.isosurface.sdf.SdfShape;
import org.openjfx.isosurface.suface.SdfMeshBuilder;
import org.openjfx.isosurface.ui.ModelViewer;
import org.openjfx.isosurface.ui.SettingsPanel;
import org.openjfx.isosurface.util.Stopwatch;

public final class Main extends Application {
    private VoxelGrid voxelGrid;
    private SettingsPanel settingsPanel;
    private ModelViewer modelViewer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        voxelGrid = new VoxelGrid();
        settingsPanel = new SettingsPanel();
        modelViewer = new ModelViewer();

        settingsPanel.getShapeSelector().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getTorusMajorRadius().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getTorusMinorRadius().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getSphereRadius().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getBoxWidth().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getBoxHeight().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getBoxDepth().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getShapeTranslationX().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getShapeTranslationY().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getShapeTranslationZ().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getShapeRotationX().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getShapeRotationY().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getShapeRotationZ().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getShapeScaleX().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getShapeScaleY().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getShapeScaleZ().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getAlgorithmSelector().valueProperty().addListener(observable -> updateMesh());
        settingsPanel.getVoxelSize().valueProperty().addListener(observable -> updateVoxelGrid());
        settingsPanel.getIsoLevel().valueProperty().addListener(observable -> updateMesh());
        settingsPanel.getSmoothShadingCheckbox().selectedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    final ObservableIntegerArray meshFaceSmoothingGroups = ((TriangleMesh) modelViewer.getModel().getMesh()).getFaceSmoothingGroups();
                    final int smoothingValue = newValue ? 1 : 0;

                    for (int i = 0; i < meshFaceSmoothingGroups.size(); i++) {
                        meshFaceSmoothingGroups.set(i, smoothingValue);
                    }
                }
        );
        settingsPanel.getWireframeCheckbox().selectedProperty().addListener(
                (observable, oldValue, newValue) -> modelViewer.setWireframe(newValue)
        );
        settingsPanel.getDegenTriThresholdValue().valueProperty().addListener(observable -> updateNumDegenerateTriangles());

        voxelGrid.voxelSizeProperty().bind(settingsPanel.getVoxelSize().valueProperty());
        modelViewer.setWireframe(settingsPanel.getWireframeCheckbox().isSelected());

        final ScrollPane settingsPanelScrollPane = new ScrollPane(settingsPanel.getRoot());
        settingsPanelScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        settingsPanelScrollPane.setFitToWidth(true);

        final PerspectiveCamera modelViewerCamera = modelViewer.getCamera();
        final SubScene modelViewerRoot = modelViewer.getRoot();
        final Pane modelViewerPane = new Pane(modelViewerRoot);

        final SplitPane splitPane = new SplitPane(settingsPanelScrollPane, modelViewerPane);
        splitPane.setDividerPositions(0.25);
        SplitPane.setResizableWithParent(settingsPanelScrollPane, false);

        final Scene scene = new Scene(splitPane, 1200, 800);
        scene.getStylesheets().add("style.css");

        modelViewerRoot.widthProperty().bind(modelViewerPane.widthProperty());
        modelViewerRoot.heightProperty().bind(scene.heightProperty());
        modelViewerCamera.verticalFieldOfViewProperty().bind(
                modelViewerRoot.widthProperty().greaterThan(modelViewerRoot.heightProperty())
        );

        updateVoxelGrid();

        stage.setTitle("Isosurface Extraction");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Recalculates the number of degenerate triangles within the mesh displayed by the model viewer.
     */
    private void updateNumDegenerateTriangles() {
        final TriangleMesh mesh = (TriangleMesh) modelViewer.getModel().getMesh();
        final double voxelSize = voxelGrid.getVoxelSize();
        final double degenTriThreshold = settingsPanel.getDegenTriThresholdValue().valueProperty().get();
        final ObservableFloatArray points = mesh.getPoints();
        final ObservableIntegerArray faces = mesh.getFaces();

        int numDegenerateTriangles = 0;

        for (int i = 0; i < faces.size(); i += 6) {
            final int p0Index = faces.get(i) * 3;
            final int p1Index = faces.get(i + 2) * 3;
            final int p2Index = faces.get(i + 4) * 3;

            final Point3D p0 = new Point3D(points.get(p0Index), points.get(p0Index + 1), points.get(p0Index + 2));
            final Point3D p1 = new Point3D(points.get(p1Index), points.get(p1Index + 1), points.get(p1Index + 2));
            final Point3D p2 = new Point3D(points.get(p2Index), points.get(p2Index + 1), points.get(p2Index + 2));

            double length0 = p0.distance(p1);
            double length1 = p1.distance(p2);
            double length2 = p2.distance(p0);

            double minEdgeLength = Math.min(Math.min(length0, length1), length2);

            if (minEdgeLength <= voxelSize * degenTriThreshold) {
                numDegenerateTriangles++;
            }
        }

        settingsPanel.setDegenerateTrianglesValue(numDegenerateTriangles);
    }

    /**
     * Refits the voxel grid to the current shape, converts it to a scalar field, then generates a surface mesh from it.
     */
    private void updateVoxelGrid() {
        final SdfShape shape = settingsPanel.getShapeSelector().getValue();
        final Stopwatch stopwatch = new Stopwatch();

        voxelGrid.fitToShape(shape);

        stopwatch.start();
        voxelGrid.voxelizeShape(shape);
        final double drawShapeDurationMs = stopwatch.getElapsedMillis();

        settingsPanel.setVoxelizationTimeValue(drawShapeDurationMs);

        updateMesh();
    }

    /**
     * Regenerates the surface mesh from the voxel grid and updates the mesh statistics.
     */
    private void updateMesh() {
        final SdfMeshBuilder meshBuilder = settingsPanel.getAlgorithmSelector().getValue();
        final float isoLevel = (float) settingsPanel.getIsoLevel().valueProperty().get();
        final boolean smoothNormals = settingsPanel.getSmoothShadingCheckbox().isSelected();
        final TriangleMesh mesh = (TriangleMesh) modelViewer.getModel().getMesh();
        final Stopwatch stopwatch = new Stopwatch();

        stopwatch.start();
        meshBuilder.buildMesh(voxelGrid, isoLevel, smoothNormals, mesh);
        final double meshBuildDurationMs = stopwatch.getElapsedMillis();

        final int numTriangles = mesh.getFaces().size() / 6;

        settingsPanel.setTotalTrianglesValue(numTriangles);
        settingsPanel.setSurfaceGenerationTimeValue(meshBuildDurationMs);

        updateNumDegenerateTriangles();
    }
}
