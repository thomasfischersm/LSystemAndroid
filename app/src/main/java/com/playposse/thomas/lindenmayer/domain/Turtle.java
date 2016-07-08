package com.playposse.thomas.lindenmayer.domain;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.playposse.thomas.lindenmayer.widgets.RenderAsyncTask;

import java.util.Stack;

/**
 * A classical turtle for drawing that keeps track of the current position, direction, color, stroke
 * width, and so on.
 */
public class Turtle {

    private static final String LOG_CAT = Turtle.class.getSimpleName();

    private final Canvas canvas;
    private final int directionIncrement;
    private final RenderAsyncTask.ProgressCallback progressCallback;
    private final Stack<State> stateStack = new Stack<>();

    private double currentX;
    private double currentY;
    private double scaleFactor;
    private int direction;
    private Paint paint;
    private int colorIndex;
    private int strokeWidth;

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
        currentX = dimension.getStartX();
        currentY = dimension.getStartY();
        paint = new Paint();
        colorIndex = 0;
        paint.setColor(Color.BLACK);
        strokeWidth = 1;
        paint.setStrokeWidth(strokeWidth);
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
        stateStack.push(new State(currentX, currentY, direction, colorIndex, strokeWidth));
    }

    public void popState() {
        if (!stateStack.empty()) {
            State state = stateStack.pop();
            currentX = state.getX();
            currentY = state.getY();
            direction = state.getDirection();
            colorIndex = state.getColorIndex();
            strokeWidth = state.getStrokeWidth();
            recreatePaint();
        }
    }

    public void markProgress() {
        progressCallback.markProgress();
    }

    public void incrementColor() {
        if (colorIndex == ColorPalette.COLORS.length - 1) {
            colorIndex = 0;
        } else {
            colorIndex++;
        }
        recreatePaint();
    }

    public void decrementColor() {
        if (colorIndex == 0) {
            colorIndex = ColorPalette.COLORS.length - 1;
        } else {
            colorIndex--;
        }
        recreatePaint();
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex % ColorPalette.COLORS.length;
        recreatePaint();
    }

    public void incrementStrokeWidth() {
        strokeWidth++;
        recreatePaint();
    }

    public void decrementStrokeWidth() {
        if (strokeWidth > 0) {
            strokeWidth--;
            recreatePaint();
        }
    }

    private void recreatePaint() {
        paint = new Paint();
        paint.setColor(ColorPalette.COLORS[colorIndex]);
        paint.setStrokeWidth(strokeWidth);
    }

    public static final class State {
        private double x;
        private double y;
        private int direction;
        private int colorIndex;
        private int strokeWidth;

        public State(double x, double y, int direction, int colorIndex, int strokeWidth) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.colorIndex = colorIndex;
            this.strokeWidth = strokeWidth;
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

        public int getColorIndex() {
            return colorIndex;
        }

        public int getStrokeWidth() {
            return strokeWidth;
        }
    }
}
