package com.tobiasfried.brewkeeper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DateSelectionDialog extends DialogFragment {

    // Member variables
    private DatePickerDialog.OnDateSetListener mListener;
    private LocalDate minDate;
    private LocalDate maxDate;
    private LocalDate date;

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
        if (minDate != null) {
            dialog.getDatePicker().setMinDate(Instant.from(minDate.atStartOfDay().atZone(ZoneId.systemDefault())).toEpochMilli());
        }
        if (maxDate != null) {
            dialog.getDatePicker().setMaxDate(Instant.from(maxDate.atStartOfDay().atZone(ZoneId.systemDefault())).toEpochMilli());
        }
        if (date != null) {
            dialog.updateDate(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        }
        return dialog;
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.mListener = listener;
    }

    public void setMinDate(LocalDate minDate) {
        this.minDate = minDate;
    }

    public void setMaxDate(LocalDate maxDate) {
        this.maxDate = maxDate;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
