package com.playposse.thomas.lindenmayer.activity.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.playposse.thomas.lindenmayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link RecyclerView.ViewHolder}
 */
class RuleSetViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.name_text_view) TextView nameTextView;
    @BindView(R.id.preview_image_view) ImageView previewImageView;

    RuleSetViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    TextView getNameTextView() {
        return nameTextView;
    }

    ImageView getPreviewImageView() {
        return previewImageView;
    }
}
