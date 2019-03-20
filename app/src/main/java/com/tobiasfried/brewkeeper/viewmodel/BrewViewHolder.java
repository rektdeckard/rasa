package com.tobiasfried.brewkeeper.viewmodel;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.tobiasfried.brewkeeper.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BrewViewHolder extends RecyclerView.ViewHolder {

    // Member views
    private TextView name;
    private TextView remainingDays;
    private ImageView stage;
    private CircularProgressBar progressBar;

    public BrewViewHolder(@NonNull View itemView) {
        super(itemView);
        // Bind views
        name = itemView.findViewById(R.id.brew_name_text_view);
        remainingDays = itemView.findViewById(R.id.remaining_time_text_view);
        stage = itemView.findViewById(R.id.stage_image_view);
        progressBar = itemView.findViewById(R.id.progress_circular);

    }
}
