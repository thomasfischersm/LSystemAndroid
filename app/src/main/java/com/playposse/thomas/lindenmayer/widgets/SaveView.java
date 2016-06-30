package com.playposse.thomas.lindenmayer.widgets;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playposse.thomas.lindenmayer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 5/20/2016.
 */
public class SaveView extends LinearLayout {

    private static final String LOG_CAT = SaveView.class.getSimpleName();

    private final List<SaveHandler> saveHandlers = new ArrayList<>();

    private EditText fileNameText;

    public SaveView(Context context) {
        super(context);

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.menu_save, this, true);

        fileNameText = (EditText) findViewById(R.id.fileNameText);
        fileNameText.requestFocus();
        fileNameText.setHint("File name");
        fileNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                fireSaveHandlers(fileNameText.getText().toString());
                return true;
            }
        });

        ImageView saveButton = (ImageView) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fireSaveHandlers(fileNameText.getText().toString());
            }
        });
    }

    public void setText(String text) {
        fileNameText.setText(text);
    }

    private void fireSaveHandlers(String fileName) {
        for (SaveHandler saveHandler : saveHandlers) {
            saveHandler.onSave(fileName);
        }
    }

    public void addSaveHandler(SaveHandler saveHandler) {
        saveHandlers.add(saveHandler);
    }

    public static interface SaveHandler {
        void onSave(String fileName);
    }
}
