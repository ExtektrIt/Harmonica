package com.andrey.guryanov.harmonica;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DialogFragmentAddTracks extends DialogFragment {
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Плейлист пуст!")
                .setMessage("Хотите добавить музыку?")
                .setPositiveButton("Да", null)
                .setNegativeButton("Нет", null)
                .create();
    }


}
