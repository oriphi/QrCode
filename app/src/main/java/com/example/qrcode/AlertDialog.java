package com.example.qrcode;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.app.AlertDialog.Builder;

import android.content.ClipboardManager;


public class AlertDialog extends AppCompatDialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState){

        final String text = "Salut comment ça va";
        Builder builder;
        builder = new Builder(getActivity());
        // Remplacer les String par des chaînes R.string
        builder.setTitle("Message: ");
        builder.setMessage(text);
        builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Je copie mon truc");

                // On récupère le clipboard manager
                /*
                final android.content.ClipboardManager clipboardManager = (ClipboardManager) myContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Source Text", text);
                clipboardManager.setPrimaryClip(clipData);
                */
            }
        });
        builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }
}