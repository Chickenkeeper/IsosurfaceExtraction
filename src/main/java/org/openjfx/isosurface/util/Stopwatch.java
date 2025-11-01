package org.openjfx.isosurface.util;

/**
 * Measures elapsed time. Primarily used for recording the speed of expensive operations.
 */
public class Stopwatch {
    private long startTime;

    /**
     * Creates a new {@code Stopwatch} instance.
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
     * Returns the number of elapsed nanoseconds since this stopwatch was started
     *
     * @return the current number of elapsed nanoseconds
     */
    public long getElapsedNanos() {
        return System.nanoTime() - startTime;
    }

    /**
     * Returns the number of elapsed milliseconds since this stopwatch was started
     *
     * @return the current number of elapsed milliseconds
     */
    public double getElapsedMillis() {
        return (double) getElapsedNanos() / 1_000_000.0;
    }
}
