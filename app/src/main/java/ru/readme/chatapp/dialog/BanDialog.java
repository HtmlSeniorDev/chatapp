package ru.readme.chatapp.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import ru.readme.chatapp.R;
import ru.readme.chatapp.object.UserType;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.SettingsHelper;

public class BanDialog {

    public static void show(UserResponse user, Context context,  OnBanClickListener listener){
        new BanDialog(user,context,listener);
    }


    private BanDialog(final UserResponse user, Context context, final OnBanClickListener listener) {
        View v = LayoutInflater.from(context).inflate(R.layout.ban_dialog,null);
        final Spinner spTime = (Spinner)v.findViewById(R.id.sp_ban_dialog_time);
        final EditText edDescr = (EditText)v.findViewById(R.id.ed_ban_dialog_description);
        final int []times;
        String[] timeNames;
        if(new SettingsHelper(context).getUserType()== UserType.TYPE_ADMIN){
            times = context.getResources().getIntArray(R.array.ban_times_values);
             timeNames = context.getResources().getStringArray(R.array.ban_times_names);
        }else{
            times = context.getResources().getIntArray(R.array.ban_times_values_moder);
           timeNames = context.getResources().getStringArray(R.array.ban_times_names_moder);
        }
        spTime.setAdapter(new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,timeNames));
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.do_ban)+" "+user.getNic())
                .setView(v)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.do_ban, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(listener!=null){
                            listener.onBanClick(user,edDescr.getText().toString(),(long)times[spTime.getSelectedItemPosition()]*1000L);
                        }
                    }
                })
                .show();
    }

    public interface OnBanClickListener{
        void onBanClick(UserResponse user, String description, long time);
    }
}
