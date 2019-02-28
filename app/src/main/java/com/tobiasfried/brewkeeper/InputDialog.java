package com.tobiasfried.brewkeeper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class InputDialog extends DialogFragment {

    private static String mDialogTitle = "dialogTitle";
    private InputSubmitListener mListener;

    // The interface to be implemented by the parent activity
    public interface InputSubmitListener {
        void onSubmitInput(DialogFragment dialog, String input);
    }

    public static InputDialog getInstance() {
        InputDialog fragment = new InputDialog();
        Bundle args = new Bundle();
        //args.putString(mDialogTitle, dialogTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_input, null))
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText e = getDialog().findViewById(R.id.add_ingredient_edit_text);
                        String name = e.getText().toString().trim();
                        mListener.onSubmitInput(InputDialog.this, name);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    public void setOnClickListener(InputSubmitListener listener) {
        this.mListener = listener;
    }

}