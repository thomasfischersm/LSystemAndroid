package com.playposse.thomas.lindenmayer.domain;

import java.util.Stack;

/**
 * Dimensions of the rendered fractal. There is a two step progress. A {@link Builder} draws every
 * line with a length of 1. When the {@link Dimension} itself is build, the size of the
 * {@link Builder} is scaled to the available size on the screen.
 *
 * <p>The {@link Builder} provides the algorithm with a type of turtle to figure out the dimensions
 * of the rendering.
 *
 * <p>The {@link Dimension} provides the measurements for the widget to actually draw to the
 * screen.
 */
public class Dimension {

    private final double scaleFactor;
    private final double startX;
    private final double startY;

    private Dimension(double scaleFactor, double startX, double startY) {
        this.scaleFactor = scaleFactor;
        this.startX = startX;
        this.startY = startY;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    /**
     * Builder that allows the algorithm to move around an infinite drawing plane.
     */
    public static final class Builder {
        private final Stack<Turtle.State> stateStack = new Stack<>();

        private double minX = 0;
        private double maxX = 0;
        private double minY = 0;
        private double maxY = 0;

        private int direction = 0;

        private double currentX = 0;
        private double currentY = 0;

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public double getCurrentX() {
            return currentX;
        }

        public double getCurrentY() {
            return currentY;
        }

        public void moveToPosition(double x, double y) {
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            currentX = x;
            currentY = y;
        }

        public void pushState() {
            stateStack.push(new Turtle.State(currentX, currentY, direction, -1, -1));
        }

        public void popState() {
            if (!stateStack.empty()) {
                Turtle.State state = stateStack.pop();
                currentX = state.getX();
                currentY = state.getY();
                direction = state.getDirection();
            }
        }

        /**
         * Scales the infinite drawing pane to the limited available screen real estate.
         */
        public Dimension build(double width, double height) {
            double widthRatio = width / (maxX - minX);
            double heightRatio = height / (maxY - minY);
            double scaleFactor = Math.min(widthRatio, heightRatio);

            // Handle division by zero cases.
            if ((widthRatio == Double.POSITIVE_INFINITY)
                    && (heightRatio == Double.POSITIVE_INFINITY)) {
                return new Dimension(1, 0, 0);
            } else if (widthRatio == Double.POSITIVE_INFINITY) {
                scaleFactor = heightRatio;
            } else if (heightRatio == Double.POSITIVE_INFINITY) {
                scaleFactor = widthRatio;
            }

            double startX = 0 - minX * scaleFactor;
            double startY = 0 - minY * scaleFactor;
            return new Dimension(scaleFactor, startX, startY);
        }
    }
}
