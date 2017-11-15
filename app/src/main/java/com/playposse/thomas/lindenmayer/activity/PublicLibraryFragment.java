package com.playposse.thomas.lindenmayer.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.firestore.FireStoreHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link Fragment} that shows {@link RuleSet}s that have been published by other users.
 */
public class PublicLibraryFragment extends Fragment {

    private static final String LOG_TAG = PublicLibraryFragment.class.getSimpleName();

    private static final int GRID_SPAN = 3;

    @BindView(R.id.rule_set_recycler_view) RecyclerView ruleSetRecyclerView;
    @BindView(R.id.loading_message_text_view) TextView loadingMessageTextView;
    @BindView(R.id.empty_message_text_view) TextView emptyMessageTextView;

    private RuleSetAdapter ruleSetAdapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_public_library, container, false);

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

        FireStoreHelper.loadRuleSets(new FireStoreHelper.LoadingCallback() {
            @Override
            public void onLoaded(Cursor cursor) {
                ruleSetAdapter.swapCursor(cursor);

                loadingMessageTextView.setVisibility(View.GONE);

                if (cursor.getCount() > 0) {
                    emptyMessageTextView.setVisibility(View.GONE);
                } else {
                    emptyMessageTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
