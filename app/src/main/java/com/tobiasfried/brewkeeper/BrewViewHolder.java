package com.tobiasfried.brewkeeper;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tobiasfried.brewkeeper.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class BrewViewHolder extends RecyclerView.ViewHolder {

    // Member views
    CardView card;
    TextView name;
    TextView remainingDays;
    TextView stage;
    ProgressBar progressBar;
    ImageView check;

    public BrewViewHolder(@NonNull final View itemView) {
        super(itemView);
        // Bind views
        card = itemView.findViewById(R.id.progress_card);
        name = itemView.findViewById(R.id.text_view_name);
        remainingDays = itemView.findViewById(R.id.text_view_status);
        stage = itemView.findViewById(R.id.text_view_stage);
        progressBar = itemView.findViewById(R.id.progress_horizontal);
        check = itemView.findViewById(R.id.image_view_check);

    }
}
