package com.playposse.thomas.lindenmayer.domain;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.playposse.thomas.lindenmayer.widgets.RenderAsyncTask;

import java.util.Stack;

/**
 * Created by Thomas on 5/19/2016.
 */
public interface Fragment {

    public String getString();

    public Dimension computeDimension(int direction, int directionIncrement);

    public void draw(Turtle turtle);

    public void drawCached(Turtle turtle);

    public int getSize();

    public static class Dimension {

        private final Stack<Turtle.State> stateStack = new Stack<>();

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

        public Dimension(
                double minX,
                double maxX,
                double minY,
                double maxY,
                double currentX,
                double currentY,
                int direction) {

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

        public void pushState() {
            stateStack.push(new Turtle.State(currentX, currentY, direction));
        }

        public void popState() {
            if (!stateStack.empty()) {
                Turtle.State state = stateStack.pop();
                currentX = state.getX();
                currentY = state.getY();
                direction = state.getDirection();
            }
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
        private final RenderAsyncTask.ProgressCallback progressCallback;
        private final Stack<State> stateStack = new Stack<>();

        private double currentX;
        private double currentY;
        private double scaleFactor;
        private int direction;
        private Paint paint;

        public Turtle(
                Canvas canvas,
                Dimension dimension,
                int directionIncrement,
                RenderAsyncTask.ProgressCallback progressCallback) {

            this.canvas = canvas;
            this.directionIncrement = directionIncrement;
            this.progressCallback = progressCallback;

            scaleFactor = dimension.getScaleFactor();
            direction = 0;
            currentX = 0 - dimension.getMinX() * scaleFactor;
            currentY = 0 - dimension.getMinY() * scaleFactor;
            paint = new Paint();
            paint.setColor(Color.BLACK);
        }

        public Turtle(
                Canvas canvas,
                double scaleFactor,
                double minX,
                double minY,
                int direction,
                int directionIncrement,
                RenderAsyncTask.ProgressCallback progressCallback) {

            this.canvas = canvas;
            this.directionIncrement = directionIncrement;
            this.progressCallback = progressCallback;
            this.scaleFactor = scaleFactor;
            this.direction = direction;
            currentX = 0 - minX * scaleFactor;
            currentY = 0 - minY * scaleFactor;
            paint = new Paint();
            paint.setColor(Color.BLACK);
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

        public Paint getPaint() {
            return paint;
        }

        public RenderAsyncTask.ProgressCallback getProgressCallback() {
            return progressCallback;
        }

        public void pushState() {
            stateStack.push(new State(currentX, currentY, direction));
        }

        public void popState() {
            if (!stateStack.empty()) {
                State state = stateStack.pop();
                currentX = state.getX();
                currentY = state.getY();
                direction = state.getDirection();
            }
        }

        public void markProgress() {
            progressCallback.markProgress();
        }

        public static final class State {
            private double x;
            private double y;
            private int direction;

            public State(double x, double y, int direction) {
                this.x = x;
                this.y = y;
                this.direction = direction;
            }

            public int getDirection() {
                return direction;
            }

            public double getX() {
                return x;
            }

            public double getY() {
                return y;
            }
        }
    }
}
