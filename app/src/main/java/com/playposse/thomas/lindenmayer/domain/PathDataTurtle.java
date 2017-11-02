package com.playposse.thomas.lindenmayer.domain;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;

import com.playposse.thomas.lindenmayer.widgets.RenderAsyncTask;

/**
 * A {@link Turtle} that generates an Android vector file.
 */
@SuppressLint("DefaultLocale")
public class PathDataTurtle extends Turtle {

    private static final String FILE_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<vector xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
            "        android:width=\"%1$ddp\"\n" +
            "        android:height=\"%2$ddp\"\n" +
            "        android:viewportHeight=\"%1$d\"\n" +
            "        android:viewportWidth=\"%1$d\">";
    private static final String FILE_FOOTER = "</vector>";
    private static final String PATH_START = "<path\n" +
            "        android:strokeColor=\"#%1$s\"\n" +
            "        android:strokeLineJoin=\"round\"\n" +
            "        android:strokeWidth=\"%2$d\"" +
            "        android:pathData=\"";
    private static final String PATH_END = "\"/>";

    private static final String MOVE_COMMAND = "M%1$f,%2$f";
    private static final String LINE_COMMAND = "L%1$f,%2$f";

    private final StringBuilder pathDataBuilder = new StringBuilder();

    private boolean isPaintDirty = true;
    private boolean isPaintClosed = true;
    private boolean isFileClosed = false;

    public PathDataTurtle(
            Canvas canvas,
            Dimension dimension,
            int directionIncrement,
            RenderAsyncTask.ProgressCallback progressCallback,
            int width,
            int height) {

        super(canvas, dimension, directionIncrement, progressCallback);

        String fileHeader = String.format(FILE_HEADER, width, height);
        pathDataBuilder.append(fileHeader);
    }

    private void startPath() {
        String color = String.format("#%06X", (0xFFFFFF & getCurrentColor()));
        String pathStart = String.format(PATH_START, color, getCurrentStrokeWidth());
        pathDataBuilder.append(pathStart);
    }

    private void endPath() {
        pathDataBuilder.append(PATH_END);
    }

    private void endFile() {
        pathDataBuilder.append(FILE_FOOTER);
    }

    @Override
    protected void recreatePaint() {
        isPaintDirty = true;
    }

    @Override
    public void drawLine(float fromX, float fromY, float toX, float toY, Paint paint) {
        if (isPaintDirty) {
            // TODO: Optimize: Check if the paint is truly different.
            if (!isPaintClosed) {
                endPath();
                isPaintClosed = false;
            }

            startPath();
            isPaintDirty = false;
        }

        // TODO: Optimize drawing to continue lines
        String moveTo = String.format(MOVE_COMMAND, fromX, fromY);
        String lineTo = String.format(LINE_COMMAND, toX, toY);
        pathDataBuilder.append(moveTo).append(lineTo);
    }

    public String getFileContent() {
        if (!isPaintClosed) {
            endPath();
            isPaintClosed = true;
        }
        if (!isFileClosed) {
            endFile();
            isFileClosed = true;
        }
        return pathDataBuilder.toString();
    }

    public Drawable getDrawable() {
        VectorDrawableCompat.createFromStream(asdf,asdf);

    }
}
