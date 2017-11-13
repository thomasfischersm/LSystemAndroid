package com.playposse.thomas.lindenmayer.activity;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;
import com.playposse.thomas.lindenmayer.contentprovider.parser.RuleSetConverter;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.glide.GlideApp;
import com.playposse.thomas.lindenmayer.glide.RuleSetResource;
import com.playposse.thomas.lindenmayer.util.RecyclerViewCursorAdapter;
import com.playposse.thomas.lindenmayer.util.SmartCursor;

/**
 * A adapter that manages the list of rule sets.
 */
class RuleSetAdapter extends RecyclerViewCursorAdapter<RuleSetViewHolder> {

    private static final int PREVIEW_ITERATION_COUNT = 4;

    private final Context context;

    RuleSetAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RuleSetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.rule_set_list_item,
                parent,
                false);
        return new RuleSetViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(RuleSetViewHolder holder, int position, Cursor cursor) {
        SmartCursor smartCursor = new SmartCursor(cursor, RuleSetTable.COLUMN_NAMES);
        final Long ruleSetId = smartCursor.getLong(RuleSetTable.ID_COLUMN);
        String ruleSetName = smartCursor.getString(RuleSetTable.NAME_COLUMN);
        String ruleSetJson = smartCursor.getString(RuleSetTable.RULE_SET_COLUMN);
        RuleSet ruleSet = RuleSetConverter.read(ruleSetJson);

        holder.getNameTextView().setText(ruleSetName);

        GlideApp.with(holder.getPreviewImageView().getContext())
                .load(new RuleSetResource(ruleSet, PREVIEW_ITERATION_COUNT, null, true))
                .into(holder.getPreviewImageView());

        holder.getPreviewImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityNavigator.startRuleSetActivity(context, ruleSetId);
            }
        });
    }
}
