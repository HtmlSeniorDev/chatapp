package ru.readme.chatapp.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import ru.readme.chatapp.R;

public class AskDialog {

    private AskDialog(Context context, String title, String ask, final OnAskDialogClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(ask)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onAskClick(true);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onAskClick(false);
                        }
                    }
                })
                .show();

    }

    public static void show(Context context, String title, String ask, final OnAskDialogClickListener listener) {
        new AskDialog(context, title, ask, listener);
    }

    public static void show(Context context, int title, String ask, final OnAskDialogClickListener listener) {
        String tl = context.getString(title);
        new AskDialog(context, tl, ask, listener);
    }

    public static void show(Context context, int title, int ask, final OnAskDialogClickListener listener) {
        String tl = context.getString(title);
        String t2 = context.getString(ask);
        new AskDialog(context, tl, t2, listener);
    }

    public interface OnAskDialogClickListener {
        void onAskClick(boolean acept);
    }
}
