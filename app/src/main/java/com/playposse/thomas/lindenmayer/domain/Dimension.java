package com.playposse.thomas.lindenmayer.domain;

import java.util.Stack;

/**
 * Created by Thomas on 7/1/2016.
 */
public class Dimension {

    private final Stack<Turtle.State> stateStack = new Stack<>();

    private double minX = 0;
    private double maxX = 0;
    private double minY = 0;
    private double maxY = 0;

    private int direction = 0;

    private double currentX = 0;
    private double currentY = 0;

    private double scaleFactor = 0;

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


    public double getScaleFactor() {
        if (!computed) {
            throw new RuntimeException("Forgot to call compute!");
        }
        return scaleFactor;
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
        computed = true;
    }
}
