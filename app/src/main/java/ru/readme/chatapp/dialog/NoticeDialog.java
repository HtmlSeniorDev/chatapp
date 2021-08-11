package ru.readme.chatapp.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ru.readme.chatapp.R;
import ru.readme.chatapp.object.responses.UserResponse;

public class NoticeDialog {

    private NoticeDialog(final UserResponse user, Context context, final OnNoticeClickListener listener) {
        View v = LayoutInflater.from(context).inflate(R.layout.notice_dialog, null);
        final EditText edDescr = (EditText) v.findViewById(R.id.ed_notice_dialog_description);
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.do_notice) + " " + user.getNic())
                .setView(v)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.send_notice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onBanClick(user, edDescr.getText().toString());
                        }
                    }
                })
                .show();
    }

    public static void show(UserResponse user, Context context, OnNoticeClickListener listener) {
        new NoticeDialog(user, context, listener);
    }

    public interface OnNoticeClickListener {
        void onBanClick(UserResponse user, String description);
    }
}
