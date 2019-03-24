package com.tobiasfried.brewkeeper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.constants.*;
import com.tobiasfried.brewkeeper.interfaces.OnRecyclerClickListener;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import androidx.annotation.NonNull;

public class BrewAdapter extends FirestoreRecyclerAdapter<Brew, BrewViewHolder> {

    // Global variables

    private List<Brew> mBrewList;
    private OnRecyclerClickListener mListener;
    private Context context;

    public BrewAdapter(@NonNull FirestoreRecyclerOptions<Brew> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public BrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card, parent, false);
        return new BrewViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull BrewViewHolder holder, int i, @NonNull Brew brew) {
        // Bind name and ColorStateList
        holder.name.setText(brew.getRecipe().getName());
        holder.progressBar.setProgressTintList(context.getColorStateList(R.color.color_states_progress));

        // Calculate and bind remaining days
        long endDate;
        if (brew.getStage() == Stage.PRIMARY) {
            endDate = brew.getSecondaryStartDate();
        } else {
            endDate = brew.getEndDate();
        }
        double days = ChronoUnit.DAYS.between(Instant.now(), Instant.ofEpochMilli(endDate));
        String remaining;
        if (days < 0) {
            remaining = "Brew ended!";
            holder.card.getLayoutParams().height = 100;
        } else if (days == 0) {
            remaining = "Ending today";
        } else {
            remaining = (int) days + " days remaining";
        }
        holder.remainingDays.setText(remaining);

        // Set progress indicators
        double totalDays;
        if (brew.getStage() == (Stage.PRIMARY)) {
            holder.stage.setText(R.string.stage_primary);
            totalDays = ChronoUnit.DAYS.between(Instant.ofEpochMilli(brew.getPrimaryStartDate()), Instant.ofEpochMilli(brew.getSecondaryStartDate()));
        } else {
            holder.stage.setText(R.string.stage_secondary);
            totalDays = ChronoUnit.DAYS.between(Instant.ofEpochMilli(brew.getSecondaryStartDate()), Instant.ofEpochMilli(brew.getEndDate()));
        }
        holder.progressBar.setProgress((int) (((totalDays - days) / totalDays) * 100));

        String brewId = getSnapshots().getSnapshot(i).getId();
        // TODO Add click listener
    }


    @Override
    public int getItemCount() {
        return mBrewList == null ? 0 : mBrewList.size();
    }

    public void setBrews(List<Brew> brewList) {
        this.mBrewList = brewList;
        notifyDataSetChanged();
    }

    public List<Brew> getBrews() {
        return this.mBrewList;
    }

    public void setOnRecyclerClickListener(OnRecyclerClickListener listener) {
        this.mListener = listener;
    }

}
