package com.tobiasfried.brewkeeper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class BrewAdapter extends ArrayAdapter<Brew> {

    public BrewAdapter(Context context, ArrayList<Brew> brews) {
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
        Instant event = currentBrew.getDate().toInstant();
        Instant now = Instant.now();
        Duration diff = Duration.between(now, event);
        long days = diff.toDays();
        String remainingDays = days + " Days Remaining";
        remainingTextView.setText(remainingDays);
//        remainingTextView.setText(currentBrew.getDate().toString());

        ImageView stageImageView = listItemView.findViewById(R.id.stage_image_view);
        if (currentBrew.getStage().equals(Stage.PRIMARY)) {
            stageImageView.setImageResource(R.drawable.ic_one);
        } else {
            stageImageView.setImageResource(R.drawable.ic_two);
        }

        // Return View
        return listItemView;
    }
}
