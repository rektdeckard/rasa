package com.tobiasfried.brewkeeper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tobiasfried.brewkeeper.data.BrewContract.BasicEntry;


public class BrewCursorAdapter extends CursorAdapter {

    public BrewCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.brew_name_text_view);
        nameTextView.setText(cursor.getString(cursor.getColumnIndex(BasicEntry.COLUMN_NAME)));

        TextView remainingTimeTextView = view.findViewById(R.id.remaining_time_text_view);
        remainingTimeTextView.setText("10 days");

        ImageView stageImage = view.findViewById(R.id.stage_image_view);
        stageImage.setImageResource(R.drawable.ic_one);
    }
}
