package com.playposse.thomas.lindenmayer.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.domain.ColorPalette;

/**
 * An adapter that provides the all the colors of the {@link ColorPalette}.
 */
public class ColorPaletteAdapter extends BaseAdapter {

    private final Context context;

    public ColorPaletteAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return ColorPalette.COLORS.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int color = ColorPalette.COLORS[position];
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.color_palette_item, parent, false);

        TextView colorSquare = (TextView) view.findViewById(R.id.colorSquare);
        colorSquare.setBackgroundColor(color);

        TextView colorLabel = (TextView) view.findViewById(R.id.colorLabel);
        colorLabel.setTextColor(
                (calculateBrightness(color) < 100) ? Color.WHITE : Color.BLACK);
//        colorLabel.setText(convertColorToString(color));
        colorLabel.setText("" + position);

        return view;
    }

    private static int calculateBrightness(int color) {
        int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};
        return (int) Math.sqrt(
                rgb[0] * rgb[0] * .241
                        + rgb[1] * rgb[1] * .691
                        + rgb[2] * rgb[2] * .068);
    }

    private static String convertColorToString(int color) {
        return String.format("#%06X", 0xFFFFFF & color);
    }
}
