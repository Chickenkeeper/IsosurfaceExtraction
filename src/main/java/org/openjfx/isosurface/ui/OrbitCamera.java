package org.openjfx.isosurface.ui;

import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * A perspective camera which rotates around the origin.
 */
public final class OrbitCamera {
    private static final int MIN_ZOOM = 20;
    private static final int MAX_ZOOM = 80;
    private static final int DEFAULT_ZOOM_LEVEL = 47;
    private static final double DEFAULT_HEIGHT = 0;
    private static final double DEFAULT_PITCH = -20;
    private static final double DEFAULT_YAW = -20;

    private final Translate zoom;
    private final Translate targetHeight;
    private final Rotate pitch;
    private final Rotate yaw;
    private final PerspectiveCamera camera;

    private int zoomLevel;

    /**
     * Creates an {@code OrbitCamera} with a specified zoom level, target height, yaw and pitch.
     *
     * @param zoomLevel the zoom level
     * @param height    the offset of the camera's target along the y-axis
     * @param yaw       the rotation of the camera's position around the y-axis
     * @param pitch     the rotation of the camera's position around the x-axis
     */
    public OrbitCamera(int zoomLevel, double height, double yaw, double pitch) {
        this.zoomLevel = 0;
        this.zoom = new Translate(0, 0, 0);
        this.targetHeight = new Translate(0, 0, 0);
        this.pitch = new Rotate(0, Rotate.X_AXIS);
        this.yaw = new Rotate(0, Rotate.Y_AXIS);
        this.camera = new PerspectiveCamera(true);
        this.camera.getTransforms().addAll(
                new Rotate(180, Rotate.Z_AXIS), // convert to y-up right-handed coordinate system(-ish)
                this.targetHeight,
                this.yaw,
                this.pitch,
                this.zoom
        );
        this.camera.setFarClip(1000);
        this.camera.setFieldOfView(75);

        setZoom(zoomLevel);
        setTargetHeight(height);
        setRotation(yaw, pitch);
    }

    /**
     * Creates an {@code OrbitCamera} with default properties.
     */
    public OrbitCamera() {
        this(DEFAULT_ZOOM_LEVEL, DEFAULT_HEIGHT, DEFAULT_YAW, DEFAULT_PITCH);
    }

    /**
     * Sets the zoom level of this orbit camera. Note that the zoom level is not the actual
     * distance of the camera from the origin, it is the increment from which that distance is derived.
     *
     * @param zoomLevel the new zoom level
     */
    private void setZoom(int zoomLevel) {
        final int newZoomLevel = Math.clamp(zoomLevel, MIN_ZOOM, MAX_ZOOM);

        if (newZoomLevel != this.zoomLevel) {
            this.zoomLevel = zoomLevel;
            // reduce the size of each distance increment as the camera moves closer to the origin, to create the illusion
            // that whatever the camera is looking at is increasing and decreasing in size linearly as the zoom level
            // changes, instead of appearing to grow quickly as the camera gets closer and slowly as it gets further away
            this.zoom.setZ(-Math.pow((double) zoomLevel / 40, 4));
        }
    }

    /**
     * Increments the zoom level.
     */
    public void incrementZoom() {
        setZoom(this.zoomLevel + 1);
    }

    /**
     * Decrements the zoom level.
     */
    public void decrementZoom() {
        setZoom(this.zoomLevel - 1);
    }

    /**
     * Sets the offset of the camera's target along the y-axis.
     *
     * @param height the new offset of the target along the y-axis
     */
    public void setTargetHeight(double height) {
        this.targetHeight.setY(-height);
    }

    /**
     * Sets the yaw and pitch of this orbit camera
     *
     * @param yaw   the rotation around the y-axis in degrees
     * @param pitch the rotation around the x-axis in degrees
     */
    private void setRotation(double yaw, double pitch) {
        this.pitch.setAngle(pitch);
        this.yaw.setAngle(yaw);
    }

    /**
     * Offsets the ywa and pitch of this orbit camera by the specified deltas.
     *
     * @param yawDelta   the number of degrees to add to the yaw
     * @param pitchDelta the number of degrees to add to the pitch
     */
    public void rotate(double yawDelta, double pitchDelta) {
        double newYaw = yaw.getAngle() + yawDelta;
        double newPitch = pitch.getAngle() + pitchDelta;

        newYaw %= 360;
        newPitch = Math.clamp(newPitch, -90, 90);

        setRotation(newYaw, newPitch);
    }

    /**
     * Returns the {@code Rotation} defining the pitch of this orbit camera.
     *
     * @return the rotation defining the pitch of this orbit camera
     */
    public Rotate getPitch() {
        return pitch;
    }

    /**
     * Returns the {@code Rotation} defining the pitch of this orbit camera.
     *
     * @return the rotation defining the pitch of this orbit camera
     */
    public Rotate getYaw() {
        return yaw;
    }

    /**
     * Returns the {@code PerspectiveCamera} used by this orbit camera
     *
     * @return the perspective camera used by this orbit camera
     */
    public PerspectiveCamera getCamera() {
        return this.camera;
    }
}
