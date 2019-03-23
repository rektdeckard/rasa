package com.tobiasfried.brewkeeper;

import android.content.Context;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class BrewAdapter extends FirestoreRecyclerAdapter<Brew, BrewAdapter.BrewHolder> {

    // Global variables

    private List<Brew> mBrewList;
    private OnRecyclerClickListener mListener;
    private Context context;

    public BrewAdapter(@NonNull FirestoreRecyclerOptions<Brew> options, Context context) {
        super(options);
        this.context = context;
    }

    // ViewHolder Class provides reference to each contained view
    class BrewHolder extends RecyclerView.ViewHolder {

        // Member views
        CardView card;
        TextView name;
        TextView remainingDays;
        TextView stage;
        ProgressBar progressBar;

        public BrewHolder(@NonNull View itemView) {
            super(itemView);
            // Bind views

            card = itemView.findViewById(R.id.progress_card);
            name = itemView.findViewById(R.id.text_view_name);
            remainingDays = itemView.findViewById(R.id.text_view_status);
            stage = itemView.findViewById(R.id.text_view_stage);
            progressBar = itemView.findViewById(R.id.progress_horizontal);

        }
    }

    @NonNull
    @Override
    public BrewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card, parent, false);
        return new BrewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BrewHolder holder, int position, @NonNull Brew currentBrew) {
        // Bind name and ColorStateList
        holder.name.setText(currentBrew.getRecipe().getName());
        holder.progressBar.setProgressTintList(context.getColorStateList(R.color.color_states_progress));

        // Calculate and bind remaining days
        long endDate;
        if (currentBrew.getStage() == Stage.PRIMARY) {
            endDate = currentBrew.getSecondaryStartDate();
        } else {
            endDate = currentBrew.getEndDate();
        }
        double days = ChronoUnit.DAYS.between(Instant.now(), Instant.ofEpochMilli(endDate));
        String remaining;
        if (days < 0) {
            remaining = "Brew ended!";
            holder.card.getLayoutParams().height = 50;
        } else if (days == 0) {
            remaining = "Ending today";
        } else {
            remaining = (int) days + " days remaining";
        }
        holder.remainingDays.setText(remaining);

        // Set progress indicators
        double totalDays;
        if (currentBrew.getStage() == (Stage.PRIMARY)) {
            holder.stage.setText(R.string.stage_primary);
            totalDays = ChronoUnit.DAYS.between(Instant.ofEpochMilli(currentBrew.getPrimaryStartDate()), Instant.ofEpochMilli(currentBrew.getSecondaryStartDate()));
        } else {
            holder.stage.setText(R.string.stage_secondary);
            totalDays = ChronoUnit.DAYS.between(Instant.ofEpochMilli(currentBrew.getSecondaryStartDate()), Instant.ofEpochMilli(currentBrew.getEndDate()));
        }
        holder.progressBar.setProgress((int) (((totalDays - days) / totalDays) * 100));
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
