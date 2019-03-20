package com.tobiasfried.brewkeeper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.constants.*;
import com.tobiasfried.brewkeeper.interfaces.OnRecyclerClickListener;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BrewAdapter extends FirestoreRecyclerAdapter<Brew, BrewAdapter.BrewHolder> {

    // Global variables

    private List<Brew> mBrewList;
    private OnRecyclerClickListener mListener;

    public BrewAdapter(@NonNull FirestoreRecyclerOptions<Brew> options) {
        super(options);
    }

    // ViewHolder Class provides reference to each contained view
    class BrewHolder extends RecyclerView.ViewHolder {

        // Member views
        TextView name;
        TextView remainingDays;
        ImageView stage;
        CircularProgressBar progressBar;

        public BrewHolder(@NonNull View itemView) {
            super(itemView);
            // Bind views
            name = itemView.findViewById(R.id.brew_name_text_view);
            remainingDays = itemView.findViewById(R.id.remaining_time_text_view);
            stage = itemView.findViewById(R.id.stage_image_view);
            progressBar = itemView.findViewById(R.id.progress_circular);

            // Set click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRecyclerViewItemClicked(getAdapterPosition(), v.getId());
                }
            });
        }
    }

    @NonNull
    @Override
    public BrewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_with_progress, parent, false);
        return new BrewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BrewHolder holder, int position, @NonNull Brew currentBrew) {
        // Bind name
        holder.name.setText(currentBrew.getRecipe().getName());

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
        } else if (days == 0) {
            remaining = "Ending today";
        } else {
            remaining = (int)days + " days remaining";
        }
        holder.remainingDays.setText(remaining);

        // Set progress indicators
        double totalDays;
        if (currentBrew.getStage() == (Stage.PRIMARY)) {
            holder.stage.setImageResource(R.drawable.ic_one);
            totalDays = ChronoUnit.DAYS.between(Instant.ofEpochMilli(currentBrew.getPrimaryStartDate()), Instant.ofEpochMilli(currentBrew.getSecondaryStartDate()));
        } else {
            holder.stage.setImageResource(R.drawable.ic_two);
            totalDays = ChronoUnit.DAYS.between(Instant.ofEpochMilli(currentBrew.getSecondaryStartDate()), Instant.ofEpochMilli(currentBrew.getEndDate()));
        }
        holder.progressBar.setProgress((int)(((totalDays - days)/totalDays) * 100));
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
