package com.tobiasfried.rasa

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle

import java.util.Objects
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class GenericPickerDialog : DialogFragment() {
    private var mListener: DialogInterface.OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(Objects.requireNonNull<FragmentActivity>(activity))
        builder.setItems(arguments!!.getInt(mResourceName), mListener)
        return builder.create()
    }

    fun setOnClickListener(listener: DialogInterface.OnClickListener) {
        this.mListener = listener
    }

    companion object {

        // Member Variables
        private val mResourceName = "arrayResourceId"

        // Instead of generic constructor
        fun newInstance(arrayResource: Int): GenericPickerDialog {
            val fragment = GenericPickerDialog()
            val args = Bundle()
            args.putInt(mResourceName, arrayResource)
            fragment.arguments = args
            return fragment
        }
    }

}
