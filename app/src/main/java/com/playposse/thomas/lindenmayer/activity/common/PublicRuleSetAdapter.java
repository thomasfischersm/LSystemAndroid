package com.playposse.thomas.lindenmayer.activity.common;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.PublicRuleSetTable;
import com.playposse.thomas.lindenmayer.glide.GlideApp;
import com.playposse.thomas.lindenmayer.util.SmartCursor;
import com.playposse.thomas.lindenmayer.util.StringUtil;

/**
 * A public version of {@link RuleSetAdapter} that adds UI for community interactions, e.g. likes
 * and author credit.
 */
public class PublicRuleSetAdapter extends RuleSetAdapter {

    public PublicRuleSetAdapter(Context context) {
        super(context);
    }

    @Override
    public RuleSetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.public_rule_set_list_item,
                parent,
                false);
        return new PublicRuleSetViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(RuleSetViewHolder baseHolder, int position, Cursor cursor) {
        PublicRuleSetViewHolder holder = (PublicRuleSetViewHolder) baseHolder;
        SmartCursor smartCursor = new SmartCursor(cursor, PublicRuleSetTable.COLUMN_NAMES);

        onBindViewHolder(holder, smartCursor);

        String authorDisplayName = smartCursor.getString(PublicRuleSetTable.AUTHOR_DISPLAY_NAME);

        final String credit;
        if (!StringUtil.isEmpty(authorDisplayName)) {
            credit = getContext().getString(R.string.credit_line, authorDisplayName);
            holder.getCreditTextView().setText(credit);
            holder.getCreditTextView().setVisibility(View.VISIBLE);
        } else {
            holder.getCreditTextView().setVisibility(View.GONE);
        }

        String authorPhotoUrl = smartCursor.getString(PublicRuleSetTable.AUTHOR_PHOTO_URL);
        if (!StringUtil.isEmpty(authorPhotoUrl)) {
            GlideApp.with(getContext())
                    .load(authorPhotoUrl)
                    .circleCrop()
                    .into(holder.getCreatorPhotoImageView());
            holder.getCreatorPhotoImageView().setVisibility(View.VISIBLE);
        } else {
            holder.getCreatorPhotoImageView().setImageDrawable(null);
            holder.getCreatorPhotoImageView().setVisibility(View.GONE);
        }
    }
}
