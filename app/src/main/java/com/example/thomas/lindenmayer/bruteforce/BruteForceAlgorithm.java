package com.example.thomas.lindenmayer.bruteforce;

import com.example.thomas.lindenmayer.domain.Fragment;
import com.example.thomas.lindenmayer.domain.RuleSet;

/**
 * Created by Thomas on 6/20/2016.
 */
public class BruteForceAlgorithm {

    public static String iterate(RuleSet ruleSet, int count) {
        String str = ruleSet.getAxiom();
        for (int i = 0; i < count; i++) {
            str = iterate(str, ruleSet);
        }
        return str;
    }

    private static String iterate(String startingString, RuleSet ruleSet) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < startingString.length(); i++) {
            char c = startingString.charAt(i);
            RuleSet.Rule rule = ruleSet.getRule("" + c);
            if (rule != null) {
                sb.append(rule.getReplacement());
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static Fragment.Dimension computeDimension(
            String str,
            int canvasWidth,
            int canvasHeight,
            int directionIncrement) {

        Fragment.Dimension dimension = new Fragment.Dimension();
        dimension.setDirection(0);

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
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

        dimension.computeScale(canvasWidth, canvasHeight);
        return dimension;
    }
//
//    public static void paint(
//            Canvas canvas,
//            RenderAsyncTask.ProgressCallback progressCallback,
//            String str,
//            RuleSet ruleSet,
//            Fragment.Dimension dimension) {
//
//        Fragment.Turtle turtle = new Fragment.Turtle(
//                canvas,
//                dimension,
//                ruleSet.getDirectionIncrement(),
//                progressCallback);
//        paint(str, turtle);
//    }

    public static void paint(String str, Fragment.Turtle turtle) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case 'f':
                case 'F':
                    double radiansDirection = Math.toRadians(turtle.getDirection());
                    double x = Math.sin(radiansDirection) * turtle.getScaleFactor() + turtle.getCurrentX();
                    double y = Math.cos(radiansDirection) * turtle.getScaleFactor() + turtle.getCurrentY();

                    if (c == 'f') {
                        // Math.max(0,...) deals with rounding issues where the coordinate could
                        // be slightly negative and be drawn off canvas.
                        float currentX = Math.max(0, (float) turtle.getCurrentX());
                        float currentY = Math.max(0, (float) turtle.getCurrentY());
                        float nextX = Math.max(0, (float) x);
                        float nextY = Math.max(0, (float) y);
                        turtle.getCanvas().drawLine(currentX, currentY, nextX, nextY, turtle.getPaint());
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

            turtle.markProgress();
        }
    }
}
