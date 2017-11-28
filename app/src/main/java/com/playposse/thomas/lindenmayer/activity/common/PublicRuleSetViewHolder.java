package com.playposse.thomas.lindenmayer.activity.common;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.playposse.thomas.lindenmayer.R;

import butterknife.BindView;

/**
 * A version of {@link RuleSetViewHolder} that adds community UI elements, like author credits and
 * likes.
 */
class PublicRuleSetViewHolder extends RuleSetViewHolder {

    @BindView(R.id.credit_text_view) TextView creditTextView;
    @BindView(R.id.creator_photo_image_view) ImageView creatorPhotoImageView;
    @BindView(R.id.like_count_text_view) TextView likeCountTextView;
    @BindView(R.id.like_image_view) ImageView likeImageView;

    PublicRuleSetViewHolder(View itemView) {
        super(itemView);
    }

    TextView getCreditTextView() {
        return creditTextView;
    }

    ImageView getCreatorPhotoImageView() {
        return creatorPhotoImageView;
    }

    TextView getLikeCountTextView() {
        return likeCountTextView;
    }

    ImageView getLikeImageView() {
        return likeImageView;
    }
}
