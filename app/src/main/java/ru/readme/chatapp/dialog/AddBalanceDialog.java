package ru.readme.chatapp.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import ru.readme.chatapp.R;

public class AddBalanceDialog {

    public AddBalanceDialog(Context context, final OnAddBalanceSelectListener listener, boolean friend) {
        View v = LayoutInflater.from(context).inflate(R.layout.add_balance, null);
        final Spinner spBalance = (Spinner) v.findViewById(R.id.sp_balance_count_dialog);
        String[] names = context.getResources().getStringArray(R.array.balance_count_monets);
        final int[] counts = context.getResources().getIntArray(R.array.balance_count);
        spBalance.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, names));
        new AlertDialog.Builder(context)
                .setTitle(friend?R.string.add_balance_friend:R.string.add_balance)
                .setView(v)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onCancelAdd();
                        }
                    }
                })
                .setPositiveButton(R.string.add_balance_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            int m = 0;
                            try {
                                m = counts[spBalance.getSelectedItemPosition()];
                            } catch (Exception e) {
                            }
                            listener.onAddBalance(m);
                        }
                    }
                }).show();
    }

    public AddBalanceDialog(Context context, final OnAddBalanceSelectListener listener) {
        View v = LayoutInflater.from(context).inflate(R.layout.add_balance, null);
        final Spinner spBalance = (Spinner) v.findViewById(R.id.sp_balance_count_dialog);
        String[] names = context.getResources().getStringArray(R.array.balance_count_monets);
        final int[] counts = context.getResources().getIntArray(R.array.balance_count);
        spBalance.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, names));
        new AlertDialog.Builder(context)
                .setTitle(R.string.add_balance)
                .setView(v)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onCancelAdd();
                        }
                    }
                })
                .setPositiveButton(R.string.add_balance_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            int m = 0;
                            try {
                                m = counts[spBalance.getSelectedItemPosition()];
                            } catch (Exception e) {
                            }
                            listener.onAddBalance(m);
                        }
                    }
                }).show();
    }

    public interface OnAddBalanceSelectListener {
        void onCancelAdd();

        void onAddBalance(int add);

    }
}
