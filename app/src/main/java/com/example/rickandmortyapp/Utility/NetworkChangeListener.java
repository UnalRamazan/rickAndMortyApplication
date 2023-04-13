package com.example.rickandmortyapp.Utility;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.example.rickandmortyapp.R;

public class NetworkChangeListener extends BroadcastReceiver {

    private boolean result = true;

    @Override
    public void onReceive(final Context context, Intent intent) {
        result = Common.isConnectedToInternet(context);

        if (!Common.isConnectedToInternet(context)) {//Internet is not Connected

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layout_dialog = LayoutInflater.from(context).inflate(R.layout.check_internet_dialog, null);
            builder.setView(layout_dialog);

            AppCompatButton btnRetry = layout_dialog.findViewById(R.id.btnRetry);

            //Show dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);

            dialog.getWindow().setGravity(Gravity.CENTER);

            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    onReceive(context, intent);
                }
            });
        }
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}