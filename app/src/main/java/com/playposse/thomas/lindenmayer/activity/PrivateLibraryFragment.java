package com.playposse.thomas.lindenmayer.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A content {@link Fragment} that shows a grid with sample rule sets.
 */
public class PrivateLibraryFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GRID_SPAN = 3;

    @BindView(R.id.rule_set_recycler_view) RecyclerView ruleSetRecyclerView;
    @BindView(R.id.empty_message_text_view) TextView emptyMessageTextView;

    private RuleSetAdapter ruleSetAdapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_private_library, container, false);

        ButterKnife.bind(this, rootView);

        // Set up RecyclerView.
        ruleSetRecyclerView.setHasFixedSize(true); // Small performance improvement.
        ruleSetRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), GRID_SPAN));
        ruleSetAdapter = new RuleSetAdapter(getContext());
        ruleSetRecyclerView.setAdapter(ruleSetAdapter);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        getLoaderManager().initLoader(Loaders.privateLibraryLoader.getLoaderId(), null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = getActivity();
        if (context == null) {
            context = getContext();
        }

        return new CursorLoader(
                context,
                RuleSetTable.CONTENT_URI,
                RuleSetTable.COLUMN_NAMES,
                RuleSetTable.TYPE_COLUMN + "=?",
                new String[]{Integer.toString(RuleSetTable.PRIVATE_TYPE)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            ruleSetAdapter.swapCursor(cursor);

            if (cursor.getCount() == 0) {
                ruleSetRecyclerView.setVisibility(View.GONE);
                emptyMessageTextView.setVisibility(View.VISIBLE);
            } else {
                ruleSetRecyclerView.setVisibility(View.VISIBLE);
                emptyMessageTextView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ruleSetAdapter.swapCursor(null);
    }
}
