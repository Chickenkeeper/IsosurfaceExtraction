module IsosurfaceExtraction.main {
    requires java.desktop;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    exports org.openjfx.isosurface;
    exports org.openjfx.isosurface.model.sdf;
    exports org.openjfx.isosurface.model.suface;
    exports org.openjfx.isosurface.model.util;
    exports org.openjfx.isosurface.model.voxel;
    exports org.openjfx.isosurface.view;
    exports org.openjfx.isosurface.viewmodel;
}
