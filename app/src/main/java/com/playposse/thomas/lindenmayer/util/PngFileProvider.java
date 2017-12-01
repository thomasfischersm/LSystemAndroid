package com.playposse.thomas.lindenmayer.util;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import java.io.FileNotFoundException;

/**
 * A {@link FileProvider} that hardcodes the content type to "image/png." Because the Glide
 * cache files don't have a file extension, the default {@link FileProvider} doesn't figure out the
 * right content type.
 */
public class PngFileProvider extends FileProvider {

    private static final String PNG_MIME_TYPE = "image/png";
    private static final String DEFAULT_FILE_NAME = "lindenmayer.png";

    @Override
    public String getType(@NonNull Uri uri) {
        return PNG_MIME_TYPE;
    }

    /**
     * Modify the file name. Glide uses cryptic file names without file extensions, e.g. '.png'.
     * For the shared files to look nice, they should have a friendly name with an extension that
     * identifies the files as image files.
     */
    @Override
    public Cursor query(
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String sortOrder) {

        MatrixCursor originalCursor =
                (MatrixCursor) super.query(uri, projection, selection, selectionArgs, sortOrder);

        if (originalCursor == null) {
            return null;
        }

        // Copy the cursor and modify the file name.
        MatrixCursor modifiedCursor = new MatrixCursor(originalCursor.getColumnNames());
        int columnCount = originalCursor.getColumnNames().length;
        for (int row = 0; row < originalCursor.getCount(); row++) {
            originalCursor.move(row);
            Object[] values = new Object[columnCount];
            for (int column = 0; column < columnCount; column++) {
                switch (originalCursor.getType(column)) {
                    case MatrixCursor.FIELD_TYPE_INTEGER:
                        values[column] = originalCursor.getInt(column);
                        break;
                    case MatrixCursor.FIELD_TYPE_STRING:
                        String columnName = originalCursor.getColumnNames()[column];
                        if (OpenableColumns.DISPLAY_NAME.equals(columnName)) {
                            values[column] = DEFAULT_FILE_NAME;
                        } else {
                            values[column] = originalCursor.getString(column);
                        }
                        break;
                }
            }

            modifiedCursor.addRow(values);
        }

        return modifiedCursor;
    }

    /**
     * Overwrites the default behavior.
     *
     * <p>Glide uses numeric file names with a meaningless file extension. For shared files, the
     * user should receive a human readable file name with a .png extension.
     *
     * <p>This is accomplished by using a trick. The Glide URI is appended a good file name. The
     * URI is passed to the other app in the intent. When the other app requests the URI, this
     * method chops off the nice file name. The result should point to the actual file.
     */
    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode)
            throws FileNotFoundException {

        String uriStr = uri.toString();
        String realStr = uriStr.substring(0, uriStr.lastIndexOf('/'));
        Uri realUri = Uri.parse(realStr);

        return super.openFile(realUri, mode);
    }
}
