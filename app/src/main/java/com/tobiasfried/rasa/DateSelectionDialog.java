package com.tobiasfried.rasa;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DateSelectionDialog extends DialogFragment {

    private static final String LOG_TAG = DateSelectionDialog.class.getSimpleName();

    // Member variables
    private DatePickerDialog.OnDateSetListener mListener;
    private Long minDate;
    private Long maxDate;
    private Long date;

    // Instead of generic constructor
    public static DateSelectionDialog getInstance() {
        DateSelectionDialog fragment = new DateSelectionDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setStyle(STYLE_NO_TITLE, R.style.AppTheme);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DateSelectionDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), mListener, year, month, day);
        if (minDate != null && minDate != 0) {
            dialog.getDatePicker().setMinDate(minDate);
            Log.i(LOG_TAG, "minDate: " + minDate.toString());
        }
        if (maxDate != null && maxDate != 0) {
            dialog.getDatePicker().setMaxDate(maxDate);
            Log.i(LOG_TAG, "maxDate: " + maxDate.toString());
        }
        if (date != null && date != 0) {
            Calendar d = Calendar.getInstance();
            d.setTimeInMillis(date);
            dialog.updateDate(d.get(Calendar.YEAR), d.get(Calendar.MONTH), d.get(Calendar.DAY_OF_MONTH));
            Log.i(LOG_TAG, "date: " + date.toString());
        }
        return dialog;
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.mListener = listener;
    }

    public void setMinDate(long minDate) {
        this.minDate = minDate;
    }

    public void setMaxDate(long maxDate) {
        this.maxDate = maxDate;
    }

    public void setDate(long date) {
        this.date = date;
    }

}
