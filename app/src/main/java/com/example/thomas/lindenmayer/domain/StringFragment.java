package com.example.thomas.lindenmayer.domain;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Thomas on 5/19/2016.
 */
public class StringFragment implements Fragment {

    private final String rawString;

    public StringFragment(String rawString) {
        this.rawString = rawString;
    }

    @Override
    public String getString() {
        return rawString;
    }

    @Override
    public Dimension computeDimension(int direction, int directionIncrement) {
        Dimension dimension = new Dimension();
        dimension.setDirection(direction);

        for (int i = 0; i < rawString.length(); i++) {
            char c = rawString.charAt(i);
            if ((c == 'f') || (c == 'F')) {
                double radiansDirection = Math.toRadians(dimension.getDirection());
                double x = Math.sin(radiansDirection);
                double y = Math.cos(radiansDirection);
                dimension.setMinX(Math.min(dimension.getMinX(), dimension.getCurrentX() + x));
                dimension.setMinY(Math.min(dimension.getMinY(), dimension.getCurrentY() + y));
                dimension.setMaxX(Math.max(dimension.getMaxX(), dimension.getCurrentX() + x));
                dimension.setMaxY(Math.max(dimension.getMaxY(), dimension.getCurrentY() + y));
                dimension.setCurrentX(dimension.getCurrentX() + x);
                dimension.setCurrentY(dimension.getCurrentY() + y);
            } else if (c == '+') {
                int newDirection = dimension.getDirection() + directionIncrement;
                if (newDirection > 360) {
                    newDirection -= 360;
                }
                dimension.setDirection(newDirection);
            } else if (c == '-') {
                int newDirection = dimension.getDirection() - directionIncrement;
                if (newDirection < 360) {
                    newDirection += 360;
                }
                dimension.setDirection(newDirection);
            } else {
                // character doesn't do anything
            }
        }

        return dimension;
    }

    @Override
    public void draw(Turtle turtle) {
        for (int i = 0; i < rawString.length(); i++) {
            char c = rawString.charAt(i);
            switch (c) {
                case 'f':
                case 'F':
                    double radiansDirection = Math.toRadians(turtle.getDirection());
                    double x = Math.sin(radiansDirection) * turtle.getScaleFactor() + turtle.getCurrentX();
                    double y = Math.cos(radiansDirection) * turtle.getScaleFactor() + turtle.getCurrentY();

                    if (c == 'f') {
                        Paint paint = new Paint();
                        paint.setColor(Color.BLACK);
                        turtle.getCanvas().drawLine((float) turtle.getCurrentX(), (float) turtle.getCurrentY(), (float) x, (float) y, paint);
                    }

                    turtle.setCurrentX(x);
                    turtle.setCurrentY(y);
                    break;
                case '+':
                    turtle.setDirection(turtle.getDirection() + turtle.getDirectionIncrement());
                    if (turtle.getDirection() > 360) {
                        turtle.setDirection(turtle.getDirection() - 360);
                    }
                    break;
                case '-':
                    turtle.setDirection(turtle.getDirection() - turtle.getDirectionIncrement());
                    if (turtle.getDirection() < 0) {
                        turtle.setDirection(turtle.getDirection() + 360);
                    }
                    break;
            }
        }
    }
}
