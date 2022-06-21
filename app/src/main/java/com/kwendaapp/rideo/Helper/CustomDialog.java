package com.kwendaapp.rideo.Helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.kwendaapp.rideo.R;

import java.util.Objects;


public class CustomDialog {
    private final Context context;
    private Dialog dialog;
    private AlertDialog.Builder builder;

    public CustomDialog(Context ctx) {
        context = ctx;
    }

    public void show() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.closeOptionsMenu();
        dialog.setCancelable(false);
        dialog.show();

        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                Toast.makeText(context, "Timeout ! Please try again later...", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();

        dialog = null;
    }

    public boolean isShowing() {
        return dialog != null;
    }


}
