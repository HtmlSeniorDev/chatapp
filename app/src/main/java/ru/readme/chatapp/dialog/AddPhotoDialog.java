package ru.readme.chatapp.dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import ru.readme.chatapp.R;
import ru.readme.chatapp.fragment.MyBaseFragment;
import ru.readme.chatapp.object.requests.PhotoRequest;
import ru.readme.chatapp.util.ImagePicker;

import static ru.readme.chatapp.dialog.SelectFileDialog.CODE_GALLERY;

public class AddPhotoDialog {

    private AlertDialog dialog;
    private EditText edDescription;
    private Button btSelectPhoto;
    private ImageView ivPhoto;
    private CheckBox chbPrivated;

    private File image;
    private MyBaseFragment context;
    private String parent;

    private OnAddPhotoListener listener;

    public AddPhotoDialog(final MyBaseFragment context, final String parent, final OnAddPhotoListener listener) {
        this.context = context;
        this.parent = parent;
        this.listener = listener;
        View v = LayoutInflater.from(context.getActivity()).inflate(R.layout.add_photo_dialog, null);
        ivPhoto = (ImageView) v.findViewById(R.id.iv_add_photo_photo);
        edDescription = (EditText) v.findViewById(R.id.ed_add_photo_name);
        btSelectPhoto = (Button) v.findViewById(R.id.bt_add_photo_select_photo);
        chbPrivated = (CheckBox) v.findViewById(R.id.chb_add_photo_dialog_private_access);
        dialog = new AlertDialog.Builder(context.getActivity())
                .setTitle(R.string.add_photo)
                .setView(v)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.add_photo_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            PhotoRequest request = new PhotoRequest();
                            request.setDescription(edDescription.getText().toString());
                            request.setParent(parent);
                            request.setPrivated(chbPrivated.isChecked());
                            listener.onPrepearAddPhoto(image, request);
                        }
                    }
                }).show();
        btSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(context.getActivity());
                context.startActivityForResult(chooseImageIntent, CODE_GALLERY);
            }
        });
    }

    public void setImage(File file){
        image = file;
        Picasso.with(context.getActivity())
                .load(image)
                .into(ivPhoto);
    }

    public interface OnAddPhotoListener {
        void onPrepearAddPhoto(File file, PhotoRequest request);
    }
}
