package com.jgtech.calories;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class RazFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.razTitle)
                .setMessage(R.string.razMessage)
                .setPositiveButton(R.string.razPositive, (dialog, which) -> {
                    Activity activity = getActivity();
                    if (activity instanceof CalculateCaloriesActivity) {
                        ((CalculateCaloriesActivity) activity).raz();
                    }
                })
                .setNegativeButton(R.string.razNegative, (dialog, which) -> dialog.cancel());

        return builder.create();
    }
}
