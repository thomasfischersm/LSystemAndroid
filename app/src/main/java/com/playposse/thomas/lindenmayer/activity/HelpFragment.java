package com.playposse.thomas.lindenmayer.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.ui.ColorPaletteAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The {@link Fragment} for the {@link HelpActivity}.
 */
public class HelpFragment extends Fragment {

    @BindView(R.id.color_palette_grid) GridView colorPaletteGridView;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_help, container, false);

        ButterKnife.bind(this, rootView);

        colorPaletteGridView.setAdapter(new ColorPaletteAdapter(getActivity()));

        return rootView;
    }
}
