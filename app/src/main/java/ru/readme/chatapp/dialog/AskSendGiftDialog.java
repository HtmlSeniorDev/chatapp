package ru.readme.chatapp.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import ru.readme.chatapp.R;

public class AskSendGiftDialog {

    private AskSendGiftDialog(Context context, String title, String ask, final OnAskSendGiftDialogClickListener listener) {
        View v = LayoutInflater.from(context).inflate(R.layout.gift_ask,null);
        TextView tvText = (TextView)v.findViewById(R.id.tv_gift_ask_text);
        final CheckBox chbAnon = (CheckBox)v.findViewById(R.id.chb_gift_ask_anon);
        tvText.setText(ask);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onAskClick(true, chbAnon.isChecked());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onAskClick(false,false);
                        }
                    }
                })
                .show();

    }

    public static void show(Context context, String title, String ask, final OnAskSendGiftDialogClickListener listener) {
        new AskSendGiftDialog(context, title, ask, listener);
    }

    public static void show(Context context, int title, String ask, final OnAskSendGiftDialogClickListener listener) {
        String tl = context.getString(title);
        new AskSendGiftDialog(context, tl, ask, listener);
    }

    public static void show(Context context, int title, int ask, final OnAskSendGiftDialogClickListener listener) {
        String tl = context.getString(title);
        String t2 = context.getString(ask);
        new AskSendGiftDialog(context, tl, t2, listener);
    }

    public interface OnAskSendGiftDialogClickListener {
        void onAskClick(boolean acept, boolean anon);
    }
}
