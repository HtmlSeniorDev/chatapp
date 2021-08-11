package ru.readme.chatapp.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ru.readme.chatapp.R;

public class ChangePasswordDialog {

    public ChangePasswordDialog(Context context, final OnChangePasswordListener listener) {
        View v = LayoutInflater.from(context).inflate(R.layout.change_password_dialog, null);
        final EditText edOld = (EditText) v.findViewById(R.id.ed_password_dialog_old);
        final EditText edNew = (EditText) v.findViewById(R.id.ed_password_dialog_new);
        final EditText edNew1 = (EditText) v.findViewById(R.id.ed_password_dialog_new_1);
        new AlertDialog.Builder(context)
                .setTitle(R.string.change_password)
                .setView(v)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onChangePassword(edOld.getText().toString(),
                                    edNew.getText().toString(),
                                    edNew1.getText().toString());
                        }
                    }
                }).show();
    }

    public interface OnChangePasswordListener {
        void onChangePassword(String oldPassword, String newPassword, String retypePassword);
    }

}
