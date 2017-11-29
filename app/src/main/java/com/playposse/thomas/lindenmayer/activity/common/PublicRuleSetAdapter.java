package com.playposse.thomas.lindenmayer.activity.common;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.PublicRuleSetTable;
import com.playposse.thomas.lindenmayer.firestore.FireAuth;
import com.playposse.thomas.lindenmayer.firestore.FireStoreLikeHelper;
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
        final PublicRuleSetViewHolder holder = (PublicRuleSetViewHolder) baseHolder;
        SmartCursor smartCursor = new SmartCursor(cursor, PublicRuleSetTable.COLUMN_NAMES);

        onBindViewHolder(holder, smartCursor);

        String authorDisplayName = smartCursor.getString(PublicRuleSetTable.AUTHOR_DISPLAY_NAME);
        final String fireStoreId = smartCursor.getString(PublicRuleSetTable.FIRE_STORE_ID);
        Integer likeCount = smartCursor.getInt(PublicRuleSetTable.LIKE_COUNT);

        // Show credit.
        final String credit;
        if (!StringUtil.isEmpty(authorDisplayName)) {
            credit = getContext().getString(R.string.credit_line, authorDisplayName);
            holder.getCreditTextView().setText(credit);
            holder.getCreditTextView().setVisibility(View.VISIBLE);
        } else {
            holder.getCreditTextView().setVisibility(View.GONE);
        }

        // Show like count.
        TextView likeCountTextView = holder.getLikeCountTextView();
        if ((likeCount != null) && (likeCount > 0)) {
            likeCountTextView.setVisibility(View.VISIBLE);
            likeCountTextView.setText(Integer.toString(likeCount));
        } else {
            likeCountTextView.setVisibility(View.GONE);
        }

        // Show creator photo.
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

        // Deal with the like icon.
        final ImageView likeImageView = holder.getLikeImageView();
        likeImageView.setTag(R.id.fire_rule_set_id_tag, fireStoreId);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // The user is not logged in. Show the empty heart.
            updateLikeImageView(likeImageView, false);
        } else {
            FireStoreLikeHelper.read(fireStoreId, new FireStoreLikeHelper.LoadCallback() {
                @Override
                public void onLoaded(boolean hasLiked) {
                    String storedId = (String) likeImageView.getTag(R.id.fire_rule_set_id_tag);

                    if ((storedId == null) || (!fireStoreId.equals(storedId))) {
                        // The view holder has been reused since this loader finished. Discard the
                        // result.
                        return;
                    }

                    updateLikeImageView(likeImageView, hasLiked);
                }
            });
        }

        likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    // Sign in first.
                    FireAuth.signIn((Activity) getContext());

                    return;
                }

                Boolean isLiked = (Boolean) likeImageView.getTag(R.id.is_liked_tag);

                if (isLiked == null) {
                    // Data hasn't loaded yet. Ignore this.
                    return;
                }

                boolean shouldBeLiked = !isLiked;
                FireStoreLikeHelper.write(fireStoreId, shouldBeLiked);
                updateLikeImageView(likeImageView, shouldBeLiked);
            }
        });
    }

    private void updateLikeImageView(ImageView likeImageView, boolean isLiked) {
        if (isLiked) {
            likeImageView.setImageResource(R.drawable.ic_favorite_red_24dp);
        } else {
            likeImageView.setImageResource(R.drawable.ic_favorite_border_red_24dp);
        }
        likeImageView.setTag(R.id.is_liked_tag, isLiked);
    }
}
