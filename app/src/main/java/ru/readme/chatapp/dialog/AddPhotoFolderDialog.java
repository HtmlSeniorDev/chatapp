package ru.readme.chatapp.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import ru.readme.chatapp.R;
import ru.readme.chatapp.object.requests.FolderRequest;

public class AddPhotoFolderDialog {

    private CheckBox chbPrivated;
    private EditText edName, edDescription;
    private Context context;
    private OnAddPhotoFolderListener listener;

    public AddPhotoFolderDialog(Context context, final OnAddPhotoFolderListener listener) {
        this.context = context;
        this.listener = listener;
        View v = LayoutInflater.from(context).inflate(R.layout.add_photo_folder_dialog, null);
        edDescription = (EditText) v.findViewById(R.id.ed_add_photo_folder_description);
        edName = (EditText) v.findViewById(R.id.ed_add_photo_folder_name);
        chbPrivated = (CheckBox) v.findViewById(R.id.chb_add_photo_folder_dialog_private_access);
        new AlertDialog.Builder(context)
                .setTitle(R.string.add_photo_folder)
                .setView(v)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.add_folder_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            FolderRequest request = new FolderRequest();
                            request.setPrivated(chbPrivated.isChecked());
                            request.setDescription(edDescription.getText().toString());
                            request.setName(edName.getText().toString());
                            listener.onPreCreatePhotoFolder(request);
                        }
                    }
                }).show();
    }

    public interface OnAddPhotoFolderListener {
        void onPreCreatePhotoFolder(FolderRequest request);
    }
}
