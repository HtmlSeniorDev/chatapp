package ru.readme.chatapp.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ru.readme.chatapp.R;

/**
 * Created by 123 on 18.12.2017.
 */

public class AddMoneyDialog {
    public AddMoneyDialog(Context context, final AddMoneyDialog.OnAddMoneyListener listener) {
        View v = LayoutInflater.from(context).inflate(R.layout.add_money_dialog, null);
        final EditText sum = (EditText) v.findViewById(R.id.sum);
        new AlertDialog.Builder(context)
                .setTitle("Добавить баланс")
                .setView(v)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onAddMoney(sum.getText().toString());
                        }
                    }
                }).show();
    }

    public interface OnAddMoneyListener {
        void onAddMoney(String sum);
    }

}

