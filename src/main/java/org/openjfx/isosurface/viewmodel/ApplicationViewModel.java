package org.openjfx.isosurface.viewmodel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import org.openjfx.isosurface.model.sdf.*;
import org.openjfx.isosurface.model.suface.Blocky;
import org.openjfx.isosurface.model.suface.MarchingCubes;
import org.openjfx.isosurface.model.suface.SdfMeshBuilder;
import org.openjfx.isosurface.model.suface.SurfaceNets;
import org.openjfx.isosurface.model.util.DoubleParameter;
import org.openjfx.isosurface.model.util.Stopwatch;
import org.openjfx.isosurface.model.voxel.VoxelGrid;

/**
 * Provides the main functionality of the application, and facilitates
 * communication between the UI and underlying data structures.
 */
public class ApplicationViewModel {
    private final VoxelGrid voxelGrid;
    private final TriangleMesh mesh;

    // shape properties
    private final ObservableList<SdfShape> shapes;
    private final IntegerProperty shapeSelectedIndex;
    private final SdfTransform shapeTransform;

    // surface properties
    private final ObservableList<SdfMeshBuilder> meshBuilders;
    private final IntegerProperty meshBuilderSelectedIndex;
    private final DoubleProperty isoLevel;
    private final BooleanProperty useSmoothShading;
    private final BooleanProperty drawWireframe;

    // statistics properties
    private final DoubleProperty triDegenThreshold;
    private final IntegerProperty triTotalNum;
    private final IntegerProperty triDegenNum;
    private final DoubleProperty timeVoxelization;
    private final DoubleProperty timeSurfaceGeneration;

    /**
     * Constructs a new {@code ApplicationViewModel} instance.
     */
    public ApplicationViewModel() {
        voxelGrid = new VoxelGrid();
        mesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);
        mesh.getTexCoords().addAll(0.0f, 0.0f); // the mesh isn't textured so it can be initialized with default texture coords

        // shape properties
        shapes = FXCollections.observableArrayList(new Torus(), new Sphere(), new Cone(), new Box());
        shapeSelectedIndex = new SimpleIntegerProperty(0);
        shapeSelectedIndex.addListener(_ -> updateVoxelGrid());
        shapeTransform = new SdfTransform();

        shapeTransform.translationXProperty().addListener(_ -> updateVoxelGrid());
        shapeTransform.translationYProperty().addListener(_ -> updateVoxelGrid());
        shapeTransform.translationZProperty().addListener(_ -> updateVoxelGrid());

        shapeTransform.rotationXProperty().addListener(_ -> updateVoxelGrid());
        shapeTransform.rotationYProperty().addListener(_ -> updateVoxelGrid());
        shapeTransform.rotationZProperty().addListener(_ -> updateVoxelGrid());

        shapeTransform.scaleXProperty().addListener(_ -> updateVoxelGrid());
        shapeTransform.scaleYProperty().addListener(_ -> updateVoxelGrid());
        shapeTransform.scaleZProperty().addListener(_ -> updateVoxelGrid());

        for (final SdfShape shape : shapes) {
            for (final DoubleParameter parameter : shape.getParameters()) {
                parameter.value().addListener(_ -> updateVoxelGrid());
            }
        }

        // surface properties
        meshBuilders = FXCollections.observableArrayList(new SurfaceNets(), new MarchingCubes(), new Blocky());
        meshBuilderSelectedIndex = new SimpleIntegerProperty(0);
        meshBuilderSelectedIndex.addListener(_ -> updateMesh());

        voxelGrid.voxelSizeProperty().addListener(_ -> updateVoxelGrid());

        isoLevel = new SimpleDoubleProperty(0.0);
        isoLevel.addListener(_ -> updateMesh());

        useSmoothShading = new SimpleBooleanProperty(true);
        drawWireframe = new SimpleBooleanProperty(true);

        // statistics properties
        triDegenThreshold = new SimpleDoubleProperty(0.05);
        triDegenThreshold.addListener(_ -> updateNumDegenerateTriangles());

        triTotalNum = new SimpleIntegerProperty(0);
        triDegenNum = new SimpleIntegerProperty(0);

        timeVoxelization = new SimpleDoubleProperty(0);
        timeSurfaceGeneration = new SimpleDoubleProperty(0);

        // initialize voxel grid and 3d model
        updateVoxelGrid();
    }

    /**
     * Gets the voxel grid used by the application.
     *
     * @return the voxel grid
     */
    public VoxelGrid getVoxelGrid() {
        return voxelGrid;
    }

    /**
     * Gets the surface mesh used by the application.
     *
     * @return the surface mesh
     */
    public TriangleMesh getMesh() {
        return mesh;
    }

    /**
     * Gets the list of shape instances used by the application.
     *
     * @return the list of shapes
     */
    public ObservableList<SdfShape> getShapes() {
        return shapes;
    }

    /**
     * Gets the value of the selected shape index property.
     *
     * @return the value of the selected shape index property
     */
    public int getShapeSelectedIndex() {
        return shapeSelectedIndex.get();
    }

    /**
     * Gets the index of the currently selected shape within the shape list.
     *
     * @return the index of the currently selected shape
     */
    public IntegerProperty shapeSelectedIndexProperty() {
        return shapeSelectedIndex;
    }

    /**
     * Gets the sdf shape transform instance.
     *
     * @return the sdf shape transform
     */
    public SdfTransform getShapeTransform() {
        return shapeTransform;
    }

    /**
     * Gets the list of mesh builder instances used by the application.
     *
     * @return the list of mesh builders
     */
    public ObservableList<SdfMeshBuilder> getMeshBuilders() {
        return meshBuilders;
    }

    /**
     * Gets the value of the selected mesh builder index property.
     *
     * @return the value of the selected mesh builder index property
     */
    public int getMeshBuilderSelectedIndex() {
        return meshBuilderSelectedIndex.get();
    }

    /**
     * Gets the index of the currently selected mesh builder within the mesh builder list.
     *
     * @return the index of the currently selected mesh builder
     */
    public IntegerProperty meshBuilderSelectedIndexProperty() {
        return meshBuilderSelectedIndex;
    }

    /**
     * Gets the value of the iso level property.
     *
     * @return the value of the iso level property
     */
    public double getIsoLevel() {
        return isoLevel.get();
    }

    /**
     * Gets the distance offset of the iso-surface.
     *
     * @return the distance offset of the iso-surface
     */
    public DoubleProperty isoLevelProperty() {
        return isoLevel;
    }

    /**
     * Gets the value of the smooth shading property.
     *
     * @return the value of the smooth shading property
     */
    public boolean isUseSmoothShading() {
        return useSmoothShading.get();
    }

    /**
     * Gets the boolean property which specifies whether the surface mesh is shaded smooth.
     *
     * @return whether the surface mesh is shaded smooth
     */
    public BooleanProperty useSmoothShadingProperty() {
        return useSmoothShading;
    }

    /**
     * Gets the value of the wireframe drawing property.
     *
     * @return the value of the wireframe drawing property
     */
    public boolean isDrawWireframe() {
        return drawWireframe.get();
    }

    /**
     * Gets the boolean property which specifies whether the surface mesh should be drawn as a wireframe.
     *
     * @return whether the surface mesh should be drawn as a wireframe
     */
    public BooleanProperty drawWireframeProperty() {
        return drawWireframe;
    }

    /**
     * Gets the value of the degenerate triangle threshold property.
     *
     * @return the value of the degenerate triangle threshold property
     */
    public double getTriDegenThreshold() {
        return triDegenThreshold.get();
    }

    /**
     * Gets the threshold below which a triangle in the surface mesh is considered to be degenerate.
     *
     * @return the degenerate triangle threshold of the surface mesh
     */
    public DoubleProperty triDegenThresholdProperty() {
        return triDegenThreshold;
    }

    /**
     * Gets the value of the total triangle number property.
     *
     * @return the value of the total triangle number property
     */
    public int getTriTotalNum() {
        return triTotalNum.get();
    }

    /**
     * Gets the total number of triangles in the surface mesh.
     *
     * @return the total number of triangles in the surface mesh
     */
    public IntegerProperty triTotalNumProperty() {
        return triTotalNum;
    }

    /**
     * Gets the value of the degenerate triangle number property.
     *
     * @return the value of the degenerate triangle number property
     */
    public int getTriDegenNum() {
        return triDegenNum.get();
    }

    /**
     * Gets the number of degenerate triangles in the surface mesh.
     *
     * @return the number of degenerate triangles in the surface mesh
     */
    public IntegerProperty triDegenNumProperty() {
        return triDegenNum;
    }

    /**
     * Gets the value of the voxelization time property.
     *
     * @return the value of the voxelization time property
     */
    public double getTimeVoxelization() {
        return timeVoxelization.get();
    }

    /**
     * Gets the length of time it took to convert the current shape into a voxel grid, in milliseconds.
     *
     * @return how many milliseconds it took to voxelize the current shape
     */
    public DoubleProperty timeVoxelizationProperty() {
        return timeVoxelization;
    }

    /**
     * Gets the value of the surface generation time property.
     *
     * @return the value of the surface generation time property
     */
    public double getTimeSurfaceGeneration() {
        return timeSurfaceGeneration.get();
    }

    /**
     * Gets the length of time it took to generate the surface mesh, in milliseconds.
     *
     * @return how many milliseconds it took to generate the surface mesh
     */
    public DoubleProperty timeSurfaceGenerationProperty() {
        return timeSurfaceGeneration;
    }

    /**
     * Refits the voxel grid to the current shape, converts it to a scalar field, then generates a surface mesh from it.
     */
    private void updateVoxelGrid() {
        final SdfShape shape = shapes.get(shapeSelectedIndex.get());
        final Stopwatch stopwatch = new Stopwatch();

        voxelGrid.fitToShape(shape, shapeTransform);

        stopwatch.start();
        voxelGrid.voxelizeShape(shape, shapeTransform);
        final double voxelizationDurationMs = stopwatch.getElapsedMillis();

        timeVoxelization.set(voxelizationDurationMs);
        updateMesh();
    }

    /**
     * Regenerates the surface mesh from the voxel grid and updates the associated mesh statistics.
     */
    private void updateMesh() {
        final SdfMeshBuilder meshBuilder = meshBuilders.get(meshBuilderSelectedIndex.get());
        final float isoLevelValue = (float) isoLevel.get();
        final boolean smoothNormals = useSmoothShading.get();
        final Stopwatch stopwatch = new Stopwatch();

        stopwatch.start();
        meshBuilder.buildMesh(voxelGrid, isoLevelValue, smoothNormals, mesh);
        final double meshBuildDurationMs = stopwatch.getElapsedMillis();

        final int numTriangles = mesh.getFaces().size() / 6;

        triTotalNum.set(numTriangles);
        timeSurfaceGeneration.set(meshBuildDurationMs);

        updateNumDegenerateTriangles();
    }

    /**
     * Calculates the number of degenerate triangles within the suface mesh.
     */
    private void updateNumDegenerateTriangles() {
        final double voxelSize = voxelGrid.getVoxelSize();
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

            if (minEdgeLength <= voxelSize * triDegenThreshold.get()) {
                numDegenerateTriangles++;
            }
        }

        triDegenNum.set(numDegenerateTriangles);
    }
}
