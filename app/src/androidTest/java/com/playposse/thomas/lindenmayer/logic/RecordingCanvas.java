package com.playposse.thomas.lindenmayer.logic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * Extends the {@link Canvas} class to record all the drawing instructions, so that a test can
 * verify that the correct drawing instructions were sent.
 */
public class RecordingCanvas extends Canvas {

    private final List<Instruction> instructions = new ArrayList<>();

    public RecordingCanvas() {
    }

    public RecordingCanvas(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
        super.drawLine(startX, startY, stopX, stopY, paint);

        instructions.add(new DrawLineInstruction(startX, startY, stopX, stopY, paint));
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public interface Instruction {}

    public static class DrawLineInstruction implements Instruction {
        private float startX;
        private float startY;
        private float stopX;
        private float stopY;
        private Paint paint;

        public DrawLineInstruction(
                float startX,
                float startY,
                float stopX,
                float stopY,
                Paint paint) {

            this.paint = paint;
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
        }

        public Paint getPaint() {
            return paint;
        }

        public float getStartX() {
            return startX;
        }

        public float getStartY() {
            return startY;
        }

        public float getStopX() {
            return stopX;
        }

        public float getStopY() {
            return stopY;
        }
    }

}
