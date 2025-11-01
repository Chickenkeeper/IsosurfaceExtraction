package org.openjfx.isosurface.ui;

import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

/**
 * Displays a 3D model. It includes an orbit camera so the user can see the model at different angles and distances.
 */
public final class ModelViewer {
    private static final double DEFAULT_WINDOW_WIDTH = 800;
    private static final double DEFAULT_WINDOW_HEIGHT = 600;
    private static final SceneAntialiasing DEFAULT_ANTIALIASING = SceneAntialiasing.BALANCED;

    private final MeshView model;
    private final OrbitCamera camera;
    private final SubScene root;

    private double mousePosXCurr;
    private double mousePosYCurr;
    private double mousePosXPrev;
    private double mousePosYPrev;

    /**
     * Creates a new {@code ModelViewer} with a specified scene width, height and antialiasing mode.
     *
     * @param width        the width of the model viewer scene
     * @param height       the height of the model viewer scene
     * @param antialiasing the type of antialiasing to use
     */
    public ModelViewer(double width, double height, SceneAntialiasing antialiasing) {
        TriangleMesh mesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);
        mesh.getTexCoords().addAll(0.0f, 0.0f); // the mesh isn't textured so it can be initialized with default texture coords

        final PhongMaterial modelMaterial = new PhongMaterial(Color.WHITE);
        modelMaterial.setSpecularPower(8.0);
        modelMaterial.setSpecularColor(new Color(0.1, 0.1, 0.1, 1.0));

        model = new MeshView();
        model.setMesh(mesh);
        model.setMaterial(modelMaterial);
        model.setCullFace(CullFace.BACK);

        camera = new OrbitCamera();

        final AmbientLight ambientLight = new AmbientLight(new Color(0.2, 0.2, 0.2, 1.0));

        final DirectionalLight directionalLight = new DirectionalLight(new Color(0.7, 0.7, 0.7, 1.0));
        directionalLight.getTransforms().addAll(
                new Rotate(180, Rotate.Z_AXIS),
                camera.getYaw(),
                camera.getPitch()
        );

        final Group subSceneRoot = new Group(camera.getCamera(), model, ambientLight, directionalLight);

        root = new SubScene(subSceneRoot, width, height, true, antialiasing);
        root.setFill(Color.BLACK);
        root.setCamera(camera.getCamera());
        root.setOnMousePressed(event -> {
            mousePosXCurr = event.getSceneX();
            mousePosYCurr = event.getSceneY();
            mousePosXPrev = event.getSceneX();
            mousePosYPrev = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            mousePosXPrev = mousePosXCurr;
            mousePosYPrev = mousePosYCurr;
            mousePosXCurr = event.getSceneX();
            mousePosYCurr = event.getSceneY();

            // rotate the camera if the left mouse button is held
            if (event.isPrimaryButtonDown()) {
                double mouseDeltaX = (mousePosXCurr - mousePosXPrev) * 0.2;
                double mouseDeltaY = (mousePosYCurr - mousePosYPrev) * 0.2;

                camera.rotate(mouseDeltaX, -mouseDeltaY);
            }
        });
        root.setOnScroll(event -> {
            // zoom the camera if the mouse wheel is scrolled
            if (event.getDeltaY() > 0) {
                camera.decrementZoom();
            } else if (event.getDeltaY() < 0) {
                camera.incrementZoom();
            }
        });
    }

    /**
     * Creates a new {@code ModelViewer} with a default scene width, height and antialiasing mode.
     */
    public ModelViewer() {
        this(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT, DEFAULT_ANTIALIASING);
    }

    /**
     * Returns the 3D model being rendered by this model viewer.
     *
     * @return the 3D model being rendered by this model viewer
     */
    public MeshView getModel() {
        return model;
    }

    /**
     * Returns the perspective camera being used by this model viewer.
     *
     * @return the perspective camera being used by this model viewer
     */
    public PerspectiveCamera getCamera() {
        return camera.getCamera();
    }

    /**
     * Returns the root node of this model viewer.
     *
     * @return the root node of this model viewer
     */
    public SubScene getRoot() {
        return root;
    }

    /**
     * sets whether the 3D model should be drawn as a wireframe.
     *
     * @param wireframe whether the 3D model should be drawn as a wireframe
     */
    public void setWireframe(boolean wireframe) {
        model.setDrawMode(wireframe ? DrawMode.LINE : DrawMode.FILL);
    }
}
