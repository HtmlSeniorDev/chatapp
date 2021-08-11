package ru.readme.chatapp.dialog;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import ir.sohreco.androidfilechooser.FileChooserDialog;
import ru.readme.chatapp.R;
import ru.readme.chatapp.util.ImagePicker;

public class SelectFileDialog implements View.OnClickListener {

    public final static int CODE_GALLERY = 23;
    private Fragment context;
    private AlertDialog dialog;
    private Button btFile, btPhoto;
    private OnFileSelectListener listener;

    private SelectFileDialog(Fragment context, OnFileSelectListener listener) {
        this.context = context;
        this.listener = listener;
        View v = LayoutInflater.from(context.getActivity()).inflate(R.layout.file_select_dialog, null);
        btFile = (Button) v.findViewById(R.id.bt_file_select_dialog_file);
        btPhoto = (Button) v.findViewById(R.id.bt_file_select_dialog_photo);
        btPhoto.setOnClickListener(this);
        btFile.setOnClickListener(this);
        dialog = new AlertDialog.Builder(context.getActivity())
                .setTitle(R.string.attachment_select)
                .setView(v)
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public static void show(Fragment context, OnFileSelectListener listener) {
        new SelectFileDialog(context, listener);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_file_select_dialog_file) {
            FileChooserDialog.Builder builder = new FileChooserDialog.Builder(FileChooserDialog.ChooserType.FILE_CHOOSER, new FileChooserDialog.ChooserListener() {
                @Override
                public void onSelect(String path) {
                    Log.e("App", path);
                    if (listener != null) {
                        listener.onFileSelect(path);
                    }
                }
            });
            try {
                builder.build().show(context.getFragmentManager(), "selectfile");
            } catch (Exception e) {
            }
            dialog.cancel();

        } else if (view.getId() == R.id.bt_file_select_dialog_photo) {
            Intent chooseImageIntent = ImagePicker.getPickImageIntent(context.getActivity());
            context.startActivityForResult(chooseImageIntent, CODE_GALLERY);
            dialog.cancel();
        }
    }

    public interface OnFileSelectListener {
        void onFileSelect(String path);
    }
}
