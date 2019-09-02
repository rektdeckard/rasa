package com.tobiasfried.rasa

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log

import java.util.Calendar
import androidx.fragment.app.DialogFragment

class DateSelectionDialog : DialogFragment() {

    // Member variables
    private var mListener: DatePickerDialog.OnDateSetListener? = null
    private var minDate: Long? = null
    private var maxDate: Long? = null
    private var date: Long? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DateSelectionDialog and return it
        val dialog = DatePickerDialog(activity!!, mListener, year, month, day)
        if (minDate != null && minDate != 0) {
            dialog.datePicker.minDate = minDate!!
            Log.i(LOG_TAG, "minDate: " + minDate!!.toString())
        }
        if (maxDate != null && maxDate != 0) {
            dialog.datePicker.maxDate = maxDate!!
            Log.i(LOG_TAG, "maxDate: " + maxDate!!.toString())
        }
        if (date != null && date != 0) {
            val d = Calendar.getInstance()
            d.timeInMillis = date!!
            dialog.updateDate(d.get(Calendar.YEAR), d.get(Calendar.MONTH), d.get(Calendar.DAY_OF_MONTH))
            Log.i(LOG_TAG, "date: " + date!!.toString())
        }
        return dialog
    }

    fun setOnDateSetListener(listener: DatePickerDialog.OnDateSetListener) {
        this.mListener = listener
    }

    fun setMinDate(minDate: Long) {
        this.minDate = minDate
    }

    fun setMaxDate(maxDate: Long) {
        this.maxDate = maxDate
    }

    fun setDate(date: Long) {
        this.date = date
    }

    companion object {

        private val LOG_TAG = DateSelectionDialog::class.java.simpleName

        // Instead of generic constructor
        val instance: DateSelectionDialog
            get() {
                val fragment = DateSelectionDialog()
                val args = Bundle()
                fragment.arguments = args
                fragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme)
                return fragment
            }
    }

}
