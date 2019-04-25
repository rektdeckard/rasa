package com.tobiasfried.rasa;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tobiasfried.rasa.R;
import com.tobiasfried.rasa.model.Brew;
import com.tobiasfried.rasa.utils.TimeUtility;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BrewViewHolder extends RecyclerView.ViewHolder {

    private static final String LOG_TAG = BrewViewHolder.class.getSimpleName();

    public boolean expanded = false;
    private int MIN_PROGRESS = 19;
    private LinearLayout.LayoutParams expandedParams;
    private LinearLayout.LayoutParams collapsedParams;

//    private float heightExpanded = TypedValue.complexToDimension((int) itemView.getContext().getResources().getDimension(R.dimen.list_item_height),
//            itemView.getResources().getDisplayMetrics());
//    private float heightCollapsed = TypedValue.complexToDimension((int) itemView.getContext().getResources().getDimension(R.dimen.list_item_height_collapsed),
//            itemView.getResources().getDisplayMetrics());
//
    private float radiusExpanded = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, itemView.getContext().getResources().getDimension(R.dimen.list_item_corner_radius),
            itemView.getResources().getDisplayMetrics());
    private float radiusCollapsed = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, itemView.getContext().getResources().getDimension(R.dimen.list_item_corner_radius_collapsed),
            itemView.getResources().getDisplayMetrics());

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
        expandedParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) itemView.getResources().getDimension(R.dimen.list_item_height));
//        Log.d(LOG_TAG, "BrewViewHolder heightExpanded: " + heightExpanded + "px, " + itemView.getResources().getDimension(R.dimen.list_item_height) + "dp");
        collapsedParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) itemView.getResources().getDimension(R.dimen.list_item_height_collapsed));
//        Log.d(LOG_TAG, "BrewViewHolder heightCollapsed: " + heightCollapsed + "px, " + itemView.getResources().getDimension(R.dimen.list_item_height_collapsed) + "dp");
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
        } else if (days == 1) {
            remainingString = "Ending tomorrow";
        } else {
            remainingString = (int) days + " days left";
        }
        remainingDays.setText(remainingString);

        // Check for complete
        switch (brew.getStage()) {
            case PRIMARY:
            case SECONDARY:
                card.setLayoutParams(expandedParams);
                card.setRadius(R.dimen.list_item_corner_radius);
                progressBar.setVisibility(View.VISIBLE);
                remainingDays.setVisibility(View.VISIBLE);
                check.setVisibility(View.INVISIBLE);
                break;
            case PAUSED:
            case COMPLETE:
                card.setLayoutParams(collapsedParams);
                card.setRadius(R.dimen.list_item_corner_radius_collapsed);
                progressBar.setVisibility(View.INVISIBLE);
                remainingDays.setVisibility(View.INVISIBLE);
                check.setVisibility(View.VISIBLE);
                break;
        }

        // Set progress indicators
        switch (brew.getStage()) {
            case PRIMARY:
            case PAUSED:
                stage.setText(R.string.stage_primary);
                break;
            case SECONDARY:
            case COMPLETE:
                stage.setText(R.string.stage_secondary);
                break;
        }

        double totalDays = TimeUtility.daysBetween(brew.getStartDate(), brew.getEndDate());
        int progress = (int) (((totalDays - days) / totalDays) * 100);
        if (progress < MIN_PROGRESS) progress = MIN_PROGRESS;
        progressBar.setProgress(progress);
        progressBar.setSecondaryProgress(progress + 1);

        //quickActions.setVisibility(expanded ? View.VISIBLE : View.GONE);

    }


}
