package com.example.thomas.lindenmayer.domain;

import android.graphics.Canvas;

/**
 * Created by Thomas on 5/19/2016.
 */
public interface Fragment {

    public String getString();

    public Dimension computeDimension(int direction, int directionIncrement);

    public void draw(Turtle turtle);

    public static class Dimension {
        private double minX = 0;
        private double maxX = 0;
        private double minY = 0;
        private double maxY = 0;

        private int direction = 0;

        private double currentX = 0;
        private double currentY = 0;

        private double scaleFactor = 0;
        private double startX = 0;
        private double startY = 0;

        private boolean computed = false;

        public Dimension(double minX, double maxX, double minY, double maxY, double currentX, double currentY, int direction) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.currentX = currentX;
            this.currentY = currentY;
            this.direction = direction;
        }

        public Dimension() {
        }

        public double getStartY() {
            if (!computed) {
                throw new RuntimeException("Forgot to call compute!");
            }
            return startY;
        }

        public double getScaleFactor() {
            if (!computed) {
                throw new RuntimeException("Forgot to call compute!");
            }
            return scaleFactor;
        }

        public double getStartX() {
            if (!computed) {
                throw new RuntimeException("Forgot to call compute!");
            }
            return startX;
        }

        public int getDirection() {
            return direction;
        }

        public double getCurrentX() {
            return currentX;
        }

        public double getCurrentY() {
            return currentY;
        }

        public void setCurrentX(double currentX) {
            this.currentX = currentX;
        }

        public void setCurrentY(double currentY) {
            this.currentY = currentY;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public void setMaxX(double maxX) {
            this.maxX = maxX;
        }

        public void setMaxY(double maxY) {
            this.maxY = maxY;
        }

        public void setMinX(double minX) {
            this.minX = minX;
        }

        public void setMinY(double minY) {
            this.minY = minY;
        }

        public double getMaxX() {
            return maxX;
        }

        public double getMaxY() {
            return maxY;
        }

        public double getMinX() {
            return minX;
        }

        public double getMinY() {
            return minY;
        }

        public void computeScale(int width, int height) {
            double widthRatio = width / (maxX - minX);
            double heightRatio = height / (maxY - minY);
            scaleFactor = Math.min(widthRatio, heightRatio);

            startX = (minX + maxX) / 2 * scaleFactor;
            startY = (minY + maxY) / 2 * scaleFactor;

            computed = true;
        }
    }

    public static class Turtle {

        private final Canvas canvas;
        private final int directionIncrement;

        private double currentX;
        private double currentY;
        private double scaleFactor;
        private int direction;

        public Turtle(Canvas canvas, Dimension dimension, int directionIncrement) {
            this.canvas = canvas;
            this.directionIncrement = directionIncrement;

            scaleFactor = dimension.getScaleFactor();
            direction = 0;
            currentX = 0 - dimension.getMinX() * scaleFactor;
            currentY = 0 - dimension.getMinY() * scaleFactor;
        }

        public Canvas getCanvas() {
            return canvas;
        }

        public double getCurrentX() {
            return currentX;
        }

        public void setCurrentX(double currentX) {
            this.currentX = currentX;
        }

        public double getCurrentY() {
            return currentY;
        }

        public void setCurrentY(double currentY) {
            this.currentY = currentY;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public double getScaleFactor() {
            return scaleFactor;
        }

        public void setScaleFactor(double scaleFactor) {
            this.scaleFactor = scaleFactor;
        }

        public int getDirectionIncrement() {
            return directionIncrement;
        }
    }
}
