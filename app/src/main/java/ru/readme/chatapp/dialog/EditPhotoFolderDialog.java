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
import ru.readme.chatapp.object.responses.FolderResponse;

public class EditPhotoFolderDialog {

    private CheckBox chbPrivated;
    private EditText edName, edDescription;

    public EditPhotoFolderDialog(Context context, final OnEditPhotoFolderListener listener,final FolderResponse folder) {

        View v = LayoutInflater.from(context).inflate(R.layout.add_photo_folder_dialog, null);
        edDescription = (EditText) v.findViewById(R.id.ed_add_photo_folder_description);
        edName = (EditText) v.findViewById(R.id.ed_add_photo_folder_name);
        edName.setText(folder.getName());
        if(folder.getDescription()!=null){
            edDescription.setText(folder.getDescription());
        }
        chbPrivated = (CheckBox) v.findViewById(R.id.chb_add_photo_folder_dialog_private_access);
        chbPrivated.setChecked(folder.isPrivated());
        new AlertDialog.Builder(context)
                .setTitle(R.string.edit_photo_folder)
                .setView(v)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.edit_folder_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            FolderRequest request = new FolderRequest();
                            request.setPrivated(chbPrivated.isChecked());
                            request.setDescription(edDescription.getText().toString());
                            request.setName(edName.getText().toString());
                            request.setId(folder.getId());
                            listener.onPreEditPhotoFolder(request);
                        }
                    }
                }).show();
    }

    public interface OnEditPhotoFolderListener {
        void onPreEditPhotoFolder(FolderRequest request);
    }
}
