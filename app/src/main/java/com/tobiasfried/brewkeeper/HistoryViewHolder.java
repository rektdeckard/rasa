package com.tobiasfried.brewkeeper;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryViewHolder extends RecyclerView.ViewHolder {

    // Member views
    @BindView(R.id.history_card) LinearLayout card;
    @BindView(R.id.text_view_name) TextView name;
    @BindView(R.id.text_view_date) TextView date;

    public HistoryViewHolder(@NonNull final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }
}
