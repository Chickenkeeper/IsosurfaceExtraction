package org.openjfx.isosurface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openjfx.isosurface.view.ModelViewerView;
import org.openjfx.isosurface.view.ShapeSettingsPanelView;
import org.openjfx.isosurface.view.StatisticsPanelView;
import org.openjfx.isosurface.view.SurfaceSettingsPanelView;
import org.openjfx.isosurface.viewmodel.ApplicationViewModel;

/**
 * The entry point of the application, responsible for creating and initializing the UI and underlying data structures.
 */
public final class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // create viewmodel
        final ApplicationViewModel applicationViewModel = new ApplicationViewModel();

        // create views
        final ShapeSettingsPanelView shapeSettingsPanelView = new ShapeSettingsPanelView(applicationViewModel);
        final SurfaceSettingsPanelView surfaceSettingsPanelView = new SurfaceSettingsPanelView(applicationViewModel);
        final StatisticsPanelView statisticsPanelView = new StatisticsPanelView(applicationViewModel);
        final ModelViewerView modelViewerView = new ModelViewerView(applicationViewModel);

        // create left side settings panel
        final VBox settingsPanelVBox = new VBox(
            new TitledPane("Shape Settings", shapeSettingsPanelView),
            new TitledPane("Surface Settings", surfaceSettingsPanelView),
            new TitledPane("Statistics", statisticsPanelView)
        );
        final ScrollPane settingsPanelView = new ScrollPane(settingsPanelVBox);
        settingsPanelView.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        settingsPanelView.setFitToWidth(true);

        // create main interface from settings panel and model viewer
        final SplitPane splitPane = new SplitPane(settingsPanelView, modelViewerView);
        SplitPane.setResizableWithParent(settingsPanelView, false);
        splitPane.setDividerPositions(0.25);

        // create scene
        final Scene scene = new Scene(splitPane, 1200, 800);
        scene.getStylesheets().add("style.css");

        // show stage
        stage.setTitle("Isosurface Extraction");
        stage.setScene(scene);
        stage.show();
    }
}
