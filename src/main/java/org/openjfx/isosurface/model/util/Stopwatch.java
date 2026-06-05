package org.openjfx.isosurface.model.util;

/**
 * Measures elapsed time. Primarily used for recording the speed of expensive operations.
 */
public class Stopwatch {
    private long startTime;

    /**
     * Constructs a new {@code Stopwatch} instance.
     */
    public Stopwatch() {
        start();
    }

    /**
     * Starts the stopwatch.
     */
    public void start() {
        startTime = System.nanoTime();
    }

    /**
     * Gets the number of elapsed nanoseconds since this stopwatch was started
     *
     * @return the number of elapsed nanoseconds
     */
    public long getElapsedNanos() {
        return System.nanoTime() - startTime;
    }

    /**
     * Gets the number of elapsed milliseconds since this stopwatch was started
     *
     * @return the number of elapsed milliseconds
     */
    public double getElapsedMillis() {
        return (double) getElapsedNanos() / 1_000_000.0;
    }
}
