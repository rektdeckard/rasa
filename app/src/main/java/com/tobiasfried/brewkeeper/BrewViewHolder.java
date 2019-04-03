package com.tobiasfried.brewkeeper;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tobiasfried.brewkeeper.R;
import com.tobiasfried.brewkeeper.constants.Stage;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.utils.TimeUtility;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BrewViewHolder extends RecyclerView.ViewHolder {

    public boolean expanded = false;

    // Member views
    @BindView(R.id.progress_card)
    CardView card;

    @BindView(R.id.text_view_name)
    TextView name;

    @BindView(R.id.text_view_status)
    TextView remainingDays;

    @BindView(R.id.text_view_stage)
    TextView stage;

    @BindView(R.id.progress_horizontal)
    ProgressBar progressBar;

    @BindView(R.id.image_view_check)
    ImageView check;

    @BindView(R.id.quick_actions)
    LinearLayout quickActions;

    @BindView(R.id.mark_complete)
    TextView markComplete;

    @BindView(R.id.details)
    TextView details;

    @BindView(R.id.delete)
    TextView delete;

    public BrewViewHolder(@NonNull final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }

    public void bind(Brew brew) {
        // Bind name and ColorStateList
        name.setText(brew.getRecipe().getName());

        // Calculate and bind remaining days
        long endDate = brew.getEndDate();
        double days = TimeUtility.daysBetween(System.currentTimeMillis(), endDate);
        String remainingString;
        if (days <= 0) {
            remainingString = "Complete";
            card.getLayoutParams().height = 140;
            progressBar.setVisibility(View.INVISIBLE);
            remainingDays.setVisibility(View.INVISIBLE);
            check.setVisibility(View.VISIBLE);
        } else if (days == 1) {
            remainingString = "Ending tomorrow";
        } else {
            remainingString = (int) days + " days left";
        }
        remainingDays.setText(remainingString);

        // Set progress indicators
        double totalDays = TimeUtility.daysBetween(brew.getStartDate(), brew.getEndDate());
        if (brew.getStage() == (Stage.PRIMARY)) {
            stage.setText(R.string.stage_primary);
        } else {
            stage.setText(R.string.stage_secondary);
        }
        int progress = (int) (((totalDays - days) / totalDays) * 100);
        progressBar.setProgress(progress);
        progressBar.setSecondaryProgress(progress + 1);

        quickActions.setVisibility(expanded ? View.VISIBLE : View.GONE);

    }


}
