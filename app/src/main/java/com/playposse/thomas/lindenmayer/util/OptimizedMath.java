package com.playposse.thomas.lindenmayer.util;

/**
 * An optimized version of {@link Math}. {@link Math} has to account for edge cases, e.g NaN. By
 * taking these edge cases off, the methods execute faster.
 * <p>
 * <p>Profiling showed that the moveToPosition method took 1.49% of the rendering time. Most of that
 * time is spent calling the math library. If that can be cut in half, it might save 1 second of
 * rendering for a complicted L-System that takes over a minute to render.
 */
public class OptimizedMath {

    public static double min(double a, double b) {
        // For reference, the commented out code below is what the Java implementation has as
        // additional overhead.
//        if (a != a) return a;   // a is NaN
//        if ((a == 0.0d) && (b == 0.0d)
//                && (Double.doubleToLongBits(b) == negativeZeroDoubleBits)) {
//            return b;
//        }
        return (a <= b) ? a : b;
    }

    public static double max(double a, double b) {
        // For reference, the commented out code below is what the Java implementation has as
        // additional overhead.
//        if (a != a) return a;   // a is NaN
//        if ((a == 0.0d) && (b == 0.0d)
//                && (Double.doubleToLongBits(a) == negativeZeroDoubleBits)) {
//            return b;
//        }
        return (a >= b) ? a : b;
    }
}
