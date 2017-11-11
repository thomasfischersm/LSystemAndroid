package com.playposse.thomas.lindenmayer.logic;

import com.playposse.thomas.lindenmayer.domain.Dimension;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.domain.Turtle;

/**
 * The algorithm that does the string re-writing, screen dimension calculation, and actual drawing.
 *
 * <p>Note: A previous version (still available in GitHub) used an optimized version. It
 * recognized that the Lindenmayer system is self similar. Say a simple system starts with a
 *  few f's and has only one rule to replace these f's. If the dimension of the first original f
 *  after all the iterations were calculated, no other original f's needed to be sized anymore. For
 *  unknown reason that optimization actually slowed down the processing. Plus, the approach
 *  wouldn't work with stochastic rule sets and state saving instructions anway.
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
            String replacement = ruleSet.getReplacement(c);
            if (replacement != null) {
                sb.append(replacement);
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
                case '|':
                    if (dimensionBuilder.getDirection() < 180) {
                        dimensionBuilder.setDirection(dimensionBuilder.getDirection() + 180);
                    } else {
                        dimensionBuilder.setDirection(dimensionBuilder.getDirection() - 180);
                    }
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
                        turtle.drawLine(currentX, currentY, nextX, nextY, turtle.getPaint());
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
                case '|':
                    if (turtle.getDirection() < 180) {
                        turtle.setDirection(turtle.getDirection() + 180);
                    } else {
                        turtle.setDirection(turtle.getDirection() - 180);
                    }
                    break;
                case '>':
                    turtle.incrementColor();
                    break;
                case '<':
                    turtle.decrementColor();
                    break;
                case '{':
                    turtle.incrementStrokeWidth();
                    break;
                case '}':
                    turtle.decrementStrokeWidth();
                    break;
                case 'c':
                    int firstDigit = -1;
                    if (i + 1 < str.length()) {
                        firstDigit = tryParse(str.charAt(i + 1));
                    }
                    int secondDigit = -1;
                    if (i + 2 < str.length()) {
                        secondDigit = tryParse(str.charAt(i + 2));
                    }
                    if ((firstDigit >= 0) && (secondDigit >= 0)) {
                        turtle.setColorIndex(firstDigit * 10 + secondDigit);
                        i += 2;
                    } else if (firstDigit >= 0) {
                        turtle.setColorIndex(firstDigit);
                        i++;
                    } else {
                        // The user hasn't entered a proper color index. Ignore this.
                    }
                    break;
                default:
                    // Don't do anything
                    break;
            }

            turtle.markProgress();
        }
    }

    private static int tryParse(char c) {
        try {
            return Integer.parseInt("" + c);
        } catch (NumberFormatException ex) {
            // Failure is handled at the end of the method anyway.
        }
        return -1;
    }
}
