package com.playposse.thomas.lindenmayer.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.data.AppPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link CardView} that shows a message and a dismiss link. The visibility of the
 * {@link CardView} is tied to a preference that remembers if the view has already been shown.
 */
public class NotificationCardView extends CardView {

    private String preferenceKey;

    @BindView(R.id.messageTextView) TextView messageTextView;
    @BindView(R.id.dismissLink) TextView dismissLink;

    public NotificationCardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public NotificationCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // Inflate layout.
        View rootView = inflate(getContext(), R.layout.notification_card_view, this);

        // Read attributes.
        TypedArray a = context
                .getTheme()
                .obtainStyledAttributes(attrs, R.styleable.NotificationCardView, 0, 0);
        String message = a.getString(R.styleable.NotificationCardView_message);
        preferenceKey = a.getString(R.styleable.NotificationCardView_preferenceKey);
        boolean shouldShow = AppPreferences.getBoolean(context, preferenceKey, true);

        // Find child views.
        ButterKnife.bind(this, rootView);

        // Apply attributes.
        setVisibility(shouldShow ? VISIBLE : GONE);
        messageTextView.setText(message);
        dismissLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onDismissClicked();
            }
        });
    }

    private void onDismissClicked() {
        setVisibility(GONE);
        AppPreferences.setBoolean(getContext(), preferenceKey, false);
    }
}
