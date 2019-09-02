package com.tobiasfried.rasa

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText

import java.util.Objects
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class InputDialog : DialogFragment() {
    private var mListener: InputSubmitListener? = null

    // The interface to be implemented by the parent activity
    interface InputSubmitListener {
        fun onSubmitInput(dialog: DialogFragment, input: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(Objects.requireNonNull<FragmentActivity>(activity))
        val inflater = requireActivity().layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_input, null))
                .setPositiveButton(R.string.add) { dialog, which ->
                    val e = getDialog()!!.findViewById<EditText>(R.id.add_ingredient_edit_text)
                    val name = e.text.toString().trim { it <= ' ' }
                    mListener!!.onSubmitInput(this@InputDialog, name)
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> dismiss() }
        return builder.create()
    }

    fun setOnClickListener(listener: InputSubmitListener) {
        this.mListener = listener
    }

    companion object {

        private val mDialogTitle = "dialogTitle"

        //args.putString(mDialogTitle, dialogTitle);
        val instance: InputDialog
            get() {
                val fragment = InputDialog()
                val args = Bundle()
                fragment.arguments = args
                return fragment
            }
    }

}