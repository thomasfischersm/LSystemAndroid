package com.playposse.thomas.lindenmayer.logic;

import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.playposse.thomas.lindenmayer.domain.Dimension;
import com.playposse.thomas.lindenmayer.domain.Turtle;
import com.playposse.thomas.lindenmayer.ui.BruteForceRenderAsyncTask;
import com.playposse.thomas.lindenmayer.ui.FractalView;
import com.playposse.thomas.lindenmayer.ui.RenderAsyncTask;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class BruteForceAlgorithmInstrumentedTest {

    @Test
    public void paint() {
        String str = "f+f+<f+{f|<f|F|f|[f]f|c0f";
        int angleIncrement = 90;
        int canvasWidth = 100;
        int canvasHeight = 100;

        Dimension dimension = BruteForceAlgorithm.computeDimension(str, canvasWidth, canvasHeight, angleIncrement);
        FractalView fractalView = new FractalView(InstrumentationRegistry.getContext());
        RenderAsyncTask.ProgressCallbackImpl progessCallback =
                new BruteForceRenderAsyncTask(null, fractalView, 0, null, null, null, null, false)
                        .createProgessCallback(str);
        RecordingCanvas canvas = new RecordingCanvas();
        Turtle turtle = new Turtle(canvas, dimension, angleIncrement, progessCallback);
        BruteForceAlgorithm.paint(str, turtle);

        List<RecordingCanvas.Instruction> instructions = canvas.getInstructions();
        assertEquals(9, instructions.size());
        assertDrawLineInstruction(instructions.get(0), 0, 0, 0, 100, Color.BLACK, 1);
        assertDrawLineInstruction(instructions.get(1), 0, 100, 100, 100, Color.BLACK, 1);
        assertDrawLineInstruction(instructions.get(2), 100, 100, 100, 0, Color.parseColor("#F44336"), 1);
        assertDrawLineInstruction(instructions.get(3), 100, 0, 0, 0, Color.parseColor("#F44336"), 2);
        assertDrawLineInstruction(instructions.get(4), 0, 0, 100, 0, Color.parseColor("#E91E63"), 2);
        assertDrawLineInstruction(instructions.get(5), 0, 0, 100, 0, Color.parseColor("#E91E63"), 2);
        assertDrawLineInstruction(instructions.get(6), 100, 0, 0, 0, Color.parseColor("#E91E63"), 2);
        assertDrawLineInstruction(instructions.get(7), 100, 0, 0, 0, Color.parseColor("#E91E63"), 2);
        assertDrawLineInstruction(instructions.get(8), 0, 0, 100, 0, Color.BLACK, 2);
    }

    private void assertDrawLineInstruction(
            RecordingCanvas.Instruction instruction,
            int startX,
            int startY,
            int stopX,
            int stopY,
            int color,
            float strokeWidth) {

        assertThat(instruction, instanceOf(RecordingCanvas.DrawLineInstruction.class));

        RecordingCanvas.DrawLineInstruction lineInstruction =
                (RecordingCanvas.DrawLineInstruction) instruction;
        assertEquals(startX, Math.round(lineInstruction.getStartX()));
        assertEquals(startY, Math.round(lineInstruction.getStartY()));
        assertEquals(stopX, Math.round(lineInstruction.getStopX()));
        assertEquals(stopY, Math.round(lineInstruction.getStopY()));
        assertEquals(color, lineInstruction.getPaint().getColor());
        assertEquals(strokeWidth, lineInstruction.getPaint().getStrokeWidth());
    }
}
