package com.tobiasfried.brewkeeper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class GenericPickerDialog extends DialogFragment {

    // Member Variables
    private static String mResourceName = "arrayResourceId";
    private DialogInterface.OnClickListener mListener;

    // Instead of generic constructor
    public static GenericPickerDialog newInstance(int arrayResource) {
        GenericPickerDialog fragment = new GenericPickerDialog();
        Bundle args = new Bundle();
        args.putInt(mResourceName, arrayResource);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setItems(getArguments().getInt(mResourceName), mListener);
        return builder.create();
    }

    public void setOnClickListener(DialogInterface.OnClickListener listener) {
        this.mListener = listener;
    }

}
