package com.playposse.thomas.lindenmayer.logic;

import com.playposse.thomas.lindenmayer.domain.Dimension;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.domain.Turtle;

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

    public static Dimension computeDimension(
            String str,
            int canvasWidth,
            int canvasHeight,
            int directionIncrement) {

        Dimension.Builder dimensionBuilder = Dimension.createBuilder();
        dimensionBuilder.setDirection(0);

        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case 'f':
                case 'F':
                    double radiansDirection = Math.toRadians(dimensionBuilder.getDirection());
                    double x = dimensionBuilder.getCurrentX() + Math.sin(radiansDirection);
                    double y = dimensionBuilder.getCurrentY() + Math.cos(radiansDirection);
                    dimensionBuilder.moveToPosition(x, y);
                    break;
                case '+':
                    int newDirection = dimensionBuilder.getDirection() + directionIncrement;
                    if (newDirection > 360) {
                        newDirection -= 360;
                    }
                    dimensionBuilder.setDirection(newDirection);
                    break;
                case '-':
                    int newDirection2 = dimensionBuilder.getDirection() - directionIncrement;
                    if (newDirection2 < 360) {
                        newDirection2 += 360;
                    }
                    dimensionBuilder.setDirection(newDirection2);
                    break;
                case '[':
                    dimensionBuilder.pushState();
                    break;
                case ']':
                    dimensionBuilder.popState();
                    break;
                default:
                    // character doesn't do anything
                    break;
            }
        }

        return dimensionBuilder.build(canvasWidth, canvasHeight);
    }

    public static void paint(String str, Turtle turtle) {
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
                case '[':
                    turtle.pushState();
                    break;
                case ']':
                    turtle.popState();
                    break;
            }

            turtle.markProgress();
        }
    }
}
