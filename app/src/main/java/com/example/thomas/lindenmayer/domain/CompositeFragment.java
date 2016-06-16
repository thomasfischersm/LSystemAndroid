package com.example.thomas.lindenmayer.domain;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 5/19/2016.
 */
public class CompositeFragment implements Fragment {

    private final List<Fragment> fragments;

    private Map<Integer, Dimension> dimensionMap = new HashMap<>();
    private Map<Integer, Bitmap> drawingCacheMap = new HashMap<>();

    public CompositeFragment(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    @Override
    public String getString() {
        StringBuilder sb = new StringBuilder();
        for (Fragment fragment : fragments) {
            sb.append(fragment.getString());
        }
        return sb.toString();
    }

    @Override
    public Dimension computeDimension(int direction, int directionIncrement) {
        if (dimensionMap.containsKey(direction)) {
            return dimensionMap.get(direction);
        }

        Dimension dimension = new Dimension();
        dimension.setDirection(direction);

        for (Fragment fragment : fragments) {
            Dimension childDimension = fragment.computeDimension(dimension.getDirection(), directionIncrement);
            dimension.setMinX(Math.min(dimension.getMinX(), dimension.getCurrentX() + childDimension.getMinX()));
            dimension.setMinY(Math.min(dimension.getMinY(), dimension.getCurrentY() + childDimension.getMinY()));
            dimension.setMaxX(Math.max(dimension.getMaxX(), dimension.getCurrentX() + childDimension.getMaxX()));
            dimension.setMaxY(Math.max(dimension.getMaxY(), dimension.getCurrentY() + childDimension.getMaxY()));
            dimension.setCurrentX(dimension.getCurrentX() + childDimension.getCurrentX());
            dimension.setCurrentY(dimension.getCurrentY() + childDimension.getCurrentY());
            dimension.setDirection(childDimension.getDirection());
        }

        dimensionMap.put(direction, dimension);

        return dimension;
    }

    @Override
    public void draw(Turtle turtle) {
        for (Fragment fragment : fragments) {
            fragment.draw(turtle);
        }
    }

    @Override
    public void drawCached(Turtle turtle) {
        Dimension dimension = dimensionMap.get(turtle.getDirection());
        Bitmap cachedBitmap = drawingCacheMap.get(turtle.getDirection());
        if (cachedBitmap == null) {
            // Create Bitmap to cache drawing.
            double width = Math.ceil((dimension.getMaxX() - dimension.getMinX()) * turtle.getScaleFactor()) + 2;
            double height = Math.ceil((dimension.getMaxY() - dimension.getMinY()) * turtle.getScaleFactor()) + 2;
            cachedBitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
            drawingCacheMap.put(turtle.getDirection(), cachedBitmap);

            // Draw to Bitmap.
            Turtle cachedTurtle = new Turtle(
                    new Canvas(cachedBitmap),
                    turtle.getScaleFactor(),
                    dimension.getMinX(),
                    dimension.getMinY(),
                    turtle.getDirection(),
                    turtle.getDirectionIncrement(),
                    turtle.getProgressCallback());
            draw(cachedTurtle);
        }

        // Draw Bitmap.
        double startX = turtle.getCurrentX() + (dimension.getMinX() * turtle.getScaleFactor()) + 1;
        double startY = turtle.getCurrentY() + (dimension.getMinY() * turtle.getScaleFactor()) + 1;
        turtle.getCanvas().drawBitmap(cachedBitmap, (float) startX, (float) startY, null);
        turtle.setCurrentX(turtle.getCurrentX() + (dimension.getCurrentX() * turtle.getScaleFactor()));
        turtle.setCurrentY(turtle.getCurrentY() + (dimension.getCurrentY() * turtle.getScaleFactor()));
    }

    @Override
    public int getSize() {
        int size = 0;
        for (Fragment f : fragments) {
            size += f.getSize();
        }
        return size;
    }
}
