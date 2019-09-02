package com.tobiasfried.rasa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.appeaser.sublimepickerlibrary.SublimePicker
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker

import java.text.DateFormat
import java.util.Locale
import java.util.TimeZone
import androidx.fragment.app.DialogFragment

class DateRangeDialog : DialogFragment() {

    // Date & Time formatter used for formatting
    // text on the switcher button
    internal var mDateFormatter: DateFormat
    internal var mTimeFormatter: DateFormat

    // Picker
    internal var mSublimePicker: SublimePicker

    // Callback to activity
    internal var mCallback: Callback? = null

    internal var mListener: SublimeListenerAdapter = object : SublimeListenerAdapter() {
        override fun onCancelled() {
            if (mCallback != null) {
                mCallback!!.onCancelled()
            }

            // Should actually be called by activity inside `Callback.onCancelled()`
            dismiss()
        }

        override fun onDateTimeRecurrenceSet(sublimeMaterialPicker: SublimePicker,
                                             selectedDate: SelectedDate,
                                             hourOfDay: Int, minute: Int,
                                             recurrenceOption: SublimeRecurrencePicker.RecurrenceOption,
                                             recurrenceRule: String) {
            if (mCallback != null) {
                mCallback!!.onDateTimeRecurrenceSet(selectedDate,
                        hourOfDay, minute, recurrenceOption, recurrenceRule)
            }

            // Should actually be called by activity inside `Callback.onCancelled()`
            dismiss()
        }
        // You can also override 'formatDate(Date)' & 'formatTime(Date)'
        // to supply custom formatters.
    }

    init {
        // Initialize formatters
        mDateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        mTimeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
        mTimeFormatter.timeZone = TimeZone.getTimeZone("GMT+0")
    }

    // Set activity callback
    fun setCallback(callback: Callback) {
        mCallback = callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mSublimePicker = inflater.inflate(R.layout.dialog_date_range, container) as SublimePicker

        // Retrieve SublimeOptions
        val arguments = arguments
        var options: SublimeOptions? = null

        // Options can be null, in which case, default
        // options are used.
        if (arguments != null) {
            options = arguments.getParcelable("SUBLIME_OPTIONS")
        }

        mSublimePicker.initializePicker(options, mListener)
        return mSublimePicker
    }

    // For communicating with the activity
    interface Callback {
        fun onCancelled()

        fun onDateTimeRecurrenceSet(selectedDate: SelectedDate,
                                    hourOfDay: Int, minute: Int,
                                    recurrenceOption: SublimeRecurrencePicker.RecurrenceOption,
                                    recurrenceRule: String)
    }

}
