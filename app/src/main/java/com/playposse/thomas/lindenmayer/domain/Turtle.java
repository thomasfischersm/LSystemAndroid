package com.playposse.thomas.lindenmayer.domain;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.playposse.thomas.lindenmayer.widgets.RenderAsyncTask;

import java.util.Stack;

/**
 * Created by Thomas on 7/1/2016.
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

    public void incrementColor() {
        if (colorIndex == ColorPalette.COLORS.length - 1) {
            colorIndex = 0;
        } else {
            colorIndex++;
        }
        paint = ColorPalette.COLORS[colorIndex];
        Log.i(LOG_CAT, "Changed color to " + paint.getColor() + " i " + colorIndex);
    }

    public void decrementColor() {
        if (colorIndex == 0) {
            colorIndex = ColorPalette.COLORS.length - 1;
        } else {
            colorIndex--;
        }
        paint = ColorPalette.COLORS[colorIndex];
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
