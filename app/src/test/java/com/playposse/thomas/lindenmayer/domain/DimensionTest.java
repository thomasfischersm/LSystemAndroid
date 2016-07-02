package com.playposse.thomas.lindenmayer.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class DimensionTest {

    @Test
    public void build_noop() {
        Dimension.Builder builder = Dimension.createBuilder();
        Dimension dimension = builder.build(300, 300);
        assertDimension(dimension, 1, 0, 0);
    }

    @Test
    public void build_singleLine() {
        Dimension.Builder builder = Dimension.createBuilder();
        builder.moveToPosition(30, 30);
        Dimension dimension = builder.build(300, 300);
        assertDimension(dimension, 10, 0, 0);
    }

    @Test
    public void build_drawNegative() {
        Dimension.Builder builder = Dimension.createBuilder();
        builder.moveToPosition(30, 30);
        builder.moveToPosition(-60, 0);
        Dimension dimension = builder.build(300, 300);
        assertDimension(dimension, 300/90.0, 200, 0);
    }


    @Test
    public void build_drawComplex() {
        Dimension.Builder builder = Dimension.createBuilder();
        builder.moveToPosition(30, 30);
        builder.moveToPosition(-60, 0);
        builder.moveToPosition(0, 0);
        builder.moveToPosition(-30, 10);
        builder.moveToPosition(30, 30);
        Dimension dimension = builder.build(300, 300);
        assertDimension(dimension, 300/90.0, 200, 0);
    }

    @Test
    public void build_testStack() {
        Dimension.Builder builder = Dimension.createBuilder();
        builder.moveToPosition(1, 10);
        builder.pushState();
        builder.moveToPosition(2, 20);
        builder.pushState();
        builder.moveToPosition(3, 30);
        builder.popState();
        assertEquals(2.0, builder.getCurrentX());
        assertEquals(20.0, builder.getCurrentY());
        builder.popState();
        assertEquals(1.0, builder.getCurrentX());
        assertEquals(10.0, builder.getCurrentY());
        builder.popState(); // Ensure that no exception happened when overrunning the stack.
        assertEquals(1.0, builder.getCurrentX());
        assertEquals(10.0, builder.getCurrentY());
    }

    private static void assertDimension(
            Dimension dimension,
            double expectedScaleFactor,
            double expectedStartX,
            double expectedStartY) {

        assertEquals(expectedStartX, dimension.getStartX());
        assertEquals(expectedStartY, dimension.getStartY());
        assertEquals(expectedScaleFactor, dimension.getScaleFactor());
    }
}
