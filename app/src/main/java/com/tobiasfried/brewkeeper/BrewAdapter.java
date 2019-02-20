package com.tobiasfried.brewkeeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tobiasfried.brewkeeper.data.Brew;
import com.tobiasfried.brewkeeper.constants.*;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BrewAdapter extends ArrayAdapter<Brew> {

    public BrewAdapter(Context context, List<Brew> brews) {
        super(context, 0, brews);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // If View can be recycled, inflate the list_item layout
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Get next Brew
        Brew currentBrew = getItem(position);

        // Bind Views to Brew Data
        TextView brewTextView = listItemView.findViewById(R.id.brew_name_text_view);
        brewTextView.setText(currentBrew.getName());

        TextView remainingTextView = listItemView.findViewById(R.id.remaining_time_text_view);

        // Find remaining time until brew is done
        // TODO figure out how to format date in API 24 (requires API 26)
        ZonedDateTime endDate;
        if (currentBrew.getStage() == Stage.PRIMARY) {
            endDate = currentBrew.getSecondaryStartDate();
        } else {
            endDate = currentBrew.getEndDate();
        }

        String remainingDays = Period.between(ZonedDateTime.now().toLocalDate(), endDate.toLocalDate()).getDays() + " Days Remaining";
        remainingTextView.setText(remainingDays);

        ImageView stageImageView = listItemView.findViewById(R.id.stage_image_view);
        if (currentBrew.getStage() == (Stage.PRIMARY)) {
            stageImageView.setImageResource(R.drawable.ic_one);
        } else {
            stageImageView.setImageResource(R.drawable.ic_two);
        }

        // Return View
        return listItemView;
    }
}
