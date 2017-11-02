package com.playposse.thomas.lindenmayer;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.GridView;

import com.playposse.thomas.lindenmayer.activity.ParentActivity;
import com.playposse.thomas.lindenmayer.widgets.ColorPaletteAdapter;

/**
 * An {@link android.app.Activity} that shows general help.
 */
public class HelpActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar helpToolbar = (Toolbar) findViewById(R.id.helpToolbar);
        setSupportActionBar(helpToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GridView colorPaletteGridView = (GridView) findViewById(R.id.colorPaletteGrid);
        colorPaletteGridView.setAdapter(new ColorPaletteAdapter(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
