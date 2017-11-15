package com.playposse.thomas.lindenmayer.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.firestore.FireStoreHelper;
import com.playposse.thomas.lindenmayer.util.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link Fragment} that shows {@link RuleSet}s that have been published by other users.
 */
public class PublicLibraryFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = PublicLibraryFragment.class.getSimpleName();

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ruleSets")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            RuleSetTableMatrixCursor cursor = new RuleSetTableMatrixCursor(task);
                            ruleSetAdapter.swapCursor(cursor);
                        } else {
                            Log.w(LOG_TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

//        getLoaderManager()
//                .initLoader(Loaders.publicLibraryLoader.getLoaderId(), null, this)
//                .forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new PublicLibraryAsyncTaskLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * An {@link AsyncTaskLoader} that loads the {@link RuleSet}s from Firestore.
     */
    private static class PublicLibraryAsyncTaskLoader extends AsyncTaskLoader<Cursor> {

        private PublicLibraryAsyncTaskLoader(@NonNull Context context) {
            super(context);
        }

        @Nullable
        @Override
        public Cursor loadInBackground() {

            return null;
        }
    }

    /**
     * A {@link MatrixCursor} that mimics the {@link RuleSetTable} from the content repository.
     */
    private static class RuleSetTableMatrixCursor extends MatrixCursor {

        private RuleSetTableMatrixCursor(Task<QuerySnapshot> task) {
            super(RuleSetTable.COLUMN_NAMES, task.getResult().size());

            QuerySnapshot result = task.getResult();

            for (DocumentSnapshot document : result.getDocuments()) {
                int id = document.getId().hashCode();
                String name = document.getString(FireStoreHelper.RULE_SET_NAME_PROPERTY);
                String json = document.getString(FireStoreHelper.RULE_SET_PROPERTY);

                if (!StringUtil.isEmpty(name) && !StringUtil.isEmpty(json)) {
                    addRow(new Object[]{
                            id,
                            name,
                            json,
                            RuleSetTable.PUBLIC_TYPE});
                }
            }
        }
    }
}
