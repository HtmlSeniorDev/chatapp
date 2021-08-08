package ru.readme.chatapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import ru.readme.chatapp.R;

public class NoticeInfoDialog {

    private Dialog dialog;

    public Dialog getDialog() {
        return dialog;
    }

    public void hide(){
        if(dialog!=null && dialog.isShowing()){
            try {
                dialog.hide();
            }catch (Exception e){}
        }
    }

    public  void show(String title, String message, Context context, final OnAceptListener listener) {
        dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener!=null){
                            listener.onAcept();
                        }
                    }
                })
                .show();
    }

    public  void show(int title, int message, Context context, final OnAceptListener listener) {
        dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener!=null){
                            listener.onAcept();
                        }
                    }
                })
                .show();
    }

    public  void show(int title, String message, Context context, final OnAceptListener listener) {
        try {
            dialog = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (listener != null) {
                                listener.onAcept();
                            }
                        }
                    })
                    .show();
        }catch (Exception e){}
    }

    public  void show(String title, int message, Context context, final OnAceptListener listener) {
        dialog =  new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener!=null){
                            listener.onAcept();
                        }
                    }
                })
                .show();
    }

    public interface OnAceptListener{
        void onAcept();
    }
}
