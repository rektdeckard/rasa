package com.tobiasfried.brewkeeper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tobiasfried.brewkeeper.data.Brew;
import com.tobiasfried.brewkeeper.constants.*;
import com.tobiasfried.brewkeeper.interfaces.OnRecyclerClickListener;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BrewAdapter extends RecyclerView.Adapter<BrewAdapter.ViewHolder> {

    // Global variables

    private List<Brew> mBrewList;
    private OnRecyclerClickListener mListener;
    private Context mContext;

    // Constructor
    public BrewAdapter(Context context, OnRecyclerClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    // ViewHolder Class provides reference to each contained view
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Member views
        TextView name;
        TextView remainingDays;
        ImageView stage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Bind views
            name = itemView.findViewById(R.id.brew_name_text_view);
            remainingDays = itemView.findViewById(R.id.remaining_time_text_view);
            stage = itemView.findViewById(R.id.stage_image_view);

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_with_progress, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get next Brew
        Brew currentBrew = mBrewList.get(position);

        // Bind name
        holder.name.setText(currentBrew.getName());

        // Calculate and bind remaining days
        ZonedDateTime endDate;
        if (currentBrew.getStage() == Stage.PRIMARY) {
            endDate = currentBrew.getSecondaryStartDate();
        } else {
            endDate = currentBrew.getEndDate();
        }
        String remainingDays = Period.between(ZonedDateTime.now().toLocalDate(), endDate.toLocalDate()).getDays() + " Days Remaining";
        holder.remainingDays.setText(remainingDays);

        if (currentBrew.getStage() == (Stage.PRIMARY)) {
            holder.stage.setImageResource(R.drawable.ic_one);
        } else {
            holder.stage.setImageResource(R.drawable.ic_two);
        }
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


}
