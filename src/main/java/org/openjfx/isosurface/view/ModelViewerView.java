package org.openjfx.isosurface.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableIntegerArray;
import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import org.openjfx.isosurface.viewmodel.ApplicationViewModel;

/**
 * Displays a 3D model. It includes an orbit camera so the user can see the model at different angles and distances.
 */
public final class ModelViewerView extends Pane {
    private static final double DEFAULT_WINDOW_WIDTH = 800;
    private static final double DEFAULT_WINDOW_HEIGHT = 600;
    private static final SceneAntialiasing DEFAULT_ANTIALIASING = SceneAntialiasing.BALANCED;
    private static final Color DIRECTIONAL_LIGHT_COLOUR_FILL = new Color(0.7, 0.7, 0.7, 1.0);
    private static final Color DIRECTIONAL_LIGHT_COLOUR_WIRE = Color.BLACK;
    private static final Color AMBIENT_LIGHT_COLOUR_FILL = new Color(0.2, 0.2, 0.2, 1.0);
    private static final Color AMBIENT_LIGHT_COLOUR_WIRE = Color.WHITE;

    private final AmbientLight ambientLight;
    private final DirectionalLight directionalLight;
    private final MeshView model;
    private final OrbitCamera camera;

    private double mousePosXCurr;
    private double mousePosYCurr;
    private double mousePosXPrev;
    private double mousePosYPrev;

    /**
     * Constructs a new {@code ModelViewer} with a default scene width, height and antialiasing mode.
     */
    public ModelViewerView(ApplicationViewModel applicationViewModel) {
        super();

        final PhongMaterial modelMaterial = new PhongMaterial(Color.WHITE);
        modelMaterial.setSpecularPower(8.0);
        modelMaterial.setSpecularColor(new Color(0.1, 0.1, 0.1, 1.0));

        model = new MeshView();
        model.setMesh(applicationViewModel.getMesh());
        model.setMaterial(modelMaterial);
        model.setCullFace(CullFace.BACK);

        camera = new OrbitCamera();

        ambientLight = new AmbientLight(AMBIENT_LIGHT_COLOUR_FILL);

        directionalLight = new DirectionalLight(DIRECTIONAL_LIGHT_COLOUR_FILL);
        directionalLight.getTransforms().addAll(
            new Rotate(180, Rotate.Z_AXIS),
            camera.getYaw(),
            camera.getPitch()
        );

        final Group subSceneRoot = new Group(camera.getCamera(), model, ambientLight, directionalLight);
        final SubScene root = new SubScene(subSceneRoot, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT, true, DEFAULT_ANTIALIASING);
        root.setFill(Color.BLACK);
        root.setCamera(camera.getCamera());
        root.setOnMousePressed(event -> {
            mousePosXCurr = event.getSceneX();
            mousePosYCurr = event.getSceneY();
            mousePosXPrev = mousePosXCurr;
            mousePosYPrev = mousePosYCurr;
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

        getChildren().add(root);
        root.widthProperty().bind(widthProperty());
        root.heightProperty().bind(heightProperty());
        camera.getCamera().verticalFieldOfViewProperty().bind(root.widthProperty().greaterThan(root.heightProperty()));

        final BooleanProperty drawWireframe = new SimpleBooleanProperty(applicationViewModel.isDrawWireframe());
        drawWireframe.bindBidirectional(applicationViewModel.drawWireframeProperty());
        drawWireframe.addListener((_, _, newValue) -> setWireframe(newValue));

        final BooleanProperty useSmoothShading = new SimpleBooleanProperty(applicationViewModel.isUseSmoothShading());
        useSmoothShading.bindBidirectional(applicationViewModel.useSmoothShadingProperty());
        useSmoothShading.addListener((_, _, newValue) -> setSmooth(newValue));

        setWireframe(drawWireframe.get());
        setSmooth(useSmoothShading.get());
    }

    /**
     * Specifies whether the 3D model should be drawn as a wireframe.
     *
     * @param wireframe whether the 3D model should be drawn as a wireframe
     */
    private void setWireframe(boolean wireframe) {
        if (wireframe) {
            model.setDrawMode(DrawMode.LINE);
            ambientLight.setColor(AMBIENT_LIGHT_COLOUR_WIRE);
            directionalLight.setColor(DIRECTIONAL_LIGHT_COLOUR_WIRE);
        } else {
            model.setDrawMode(DrawMode.FILL);
            ambientLight.setColor(AMBIENT_LIGHT_COLOUR_FILL);
            directionalLight.setColor(DIRECTIONAL_LIGHT_COLOUR_FILL);
        }
    }

    /**
     * Enables or disables smooth shading on the 3D model.
     *
     * @param isSmooth whether the surface of the model should be smooth
     */
    private void setSmooth(boolean isSmooth) {
        final TriangleMesh mesh = (TriangleMesh) model.getMesh();
        final ObservableIntegerArray meshFaceSmoothingGroups = mesh.getFaceSmoothingGroups();
        final int smoothingValue = isSmooth ? 1 : 0;

        for (int i = 0; i < meshFaceSmoothingGroups.size(); i++) {
            meshFaceSmoothingGroups.set(i, smoothingValue);
        }
    }
}
