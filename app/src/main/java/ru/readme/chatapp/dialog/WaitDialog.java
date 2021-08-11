package ru.readme.chatapp.dialog;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;

import ru.readme.chatapp.R;

public class WaitDialog {
    private Context context;
    private AlertDialog dialog;

    public WaitDialog(Context context) {
        this.context = context;
        dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.please_wait)
                .setView(R.layout.loading_dialog)
                .setCancelable(false)
                .create();
    }

    public void show() {
        dialog.show();
    }

    public void hide() {
        dialog.cancel();
    }
}
