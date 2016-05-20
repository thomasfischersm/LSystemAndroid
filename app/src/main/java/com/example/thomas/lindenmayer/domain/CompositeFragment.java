package com.example.thomas.lindenmayer.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 5/19/2016.
 */
public class CompositeFragment implements Fragment {

    private final List<Fragment> fragments;

    private Map<Integer, Dimension> dimensionMap = new HashMap<>();

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
}
