package com.example.qrcode;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.app.AlertDialog.Builder;

import android.content.ClipboardManager;

import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;



public class AlertDialog extends AppCompatDialogFragment {

    private String text;

    public AlertDialog()
    {
        super();
        this.text = "Error";
    }

    public AlertDialog(String text) {
        super();
        this.text = text;

    }
    public String getText()
    {
        return this.text;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){

        Builder builder;
        builder = new Builder(getActivity());
        // Remplacer les String par des chaînes R.string
        builder.setTitle("Message: ");
        builder.setMessage(text);
        builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = AlertDialog.this.getText();
                /*
                if ("http".equals(text.substring(0,4)))
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
                    startActivity(browserIntent);
                }
                */
                ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Results", text);
                manager.setPrimaryClip(clip);
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