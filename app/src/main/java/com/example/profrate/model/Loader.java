package com.example.profrate.model;

import android.app.Dialog;
import android.content.Context;

import com.example.profrate.R;

public class Loader {
    private Dialog dialog;

    public Loader(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.lottie_loader);
        dialog.setCancelable(false); // Prevent dismissal by back button
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}