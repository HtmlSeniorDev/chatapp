package ru.readme.chatapp.dialog;

import android.content.DialogInterface;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import ir.sohreco.androidfilechooser.FileChooserDialog;
import ru.readme.chatapp.activity.App;
import ru.readme.chatapp.R;
import ru.readme.chatapp.object.responses.AvatarResponse;

public class AddAvatarDialog implements View.OnClickListener {

    Fragment context;
    private OnAvatarDialogAddListener listener;
    private EditText edName, edPrice;
    private Button btPhoto;
    private ImageView ivPhoto;
    private File f = null;

    public AddAvatarDialog(Fragment context, final OnAvatarDialogAddListener listener) {
        this.context = context;
        this.listener = listener;
        View v = LayoutInflater.from(context.getActivity()).inflate(R.layout.add_avatar_dialog, null);
        edName = (EditText) v.findViewById(R.id.ed_add_avatar_name);
        edPrice = (EditText) v.findViewById(R.id.ed_add_avatar_price);
        ivPhoto = (ImageView) v.findViewById(R.id.iv_add_avatar_avatar);
        btPhoto = (Button) v.findViewById(R.id.bt_add_avatar_select_photo);
        btPhoto.setOnClickListener(this);
        new AlertDialog.Builder(context.getActivity())
                .setTitle(R.string.add_avatar)
                .setView(v)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = edName.getText().toString();
                        String pr = edPrice.getText().toString();
                        int price = 0;
                        try {
                            price = Integer.parseInt(pr) * 100;
                        } catch (Exception e) {

                        }
                        if (listener != null) {
                            listener.onPrepearAvatar(name, price, f);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public AddAvatarDialog(Fragment context, final OnAvatarDialogUpdateListener listener, final AvatarResponse avatar) {
        this.context = context;
        final OnAvatarDialogUpdateListener l = listener;
        View v = LayoutInflater.from(context.getActivity()).inflate(R.layout.add_avatar_dialog, null);
        edName = (EditText) v.findViewById(R.id.ed_add_avatar_name);
        edPrice = (EditText) v.findViewById(R.id.ed_add_avatar_price);
        ivPhoto = (ImageView) v.findViewById(R.id.iv_add_avatar_avatar);
        edPrice.setText((int)(avatar.getPrice()/100)+"");
        edName.setText(avatar.getName());
        Picasso.with(context.getActivity())
                .load(new File(App.avasPath+avatar.getId()))
                .error(R.drawable.ic_portrait_deep_orange_700_48dp)
                .into(ivPhoto);
        btPhoto = (Button) v.findViewById(R.id.bt_add_avatar_select_photo);
        btPhoto.setOnClickListener(this);
        new AlertDialog.Builder(context.getActivity())
                .setTitle(R.string.update_avatar)
                .setView(v)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = edName.getText().toString();
                        String pr = edPrice.getText().toString();
                        int price = 0;
                        try {
                            price = Integer.parseInt(pr) * 100;
                        } catch (Exception e) {

                        }
                        if (l != null) {
                            l.onPrepearAvatar(avatar.getId(),name, price, f);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_add_avatar_select_photo) {
            String[] formats = {"png"};
            FileChooserDialog.Builder builder = new FileChooserDialog.Builder(FileChooserDialog.ChooserType.FILE_CHOOSER, new FileChooserDialog.ChooserListener() {
                @Override
                public void onSelect(String path) {
                    Log.e("App", path);
                    f = new File(path);
                    Picasso.with(context.getActivity())
                            .load(f)
                            .into(ivPhoto);
                }
            }).setFileFormats(formats);
            try {
                builder.build().show(context.getFragmentManager(), "selectfile");
            } catch (Exception e) {
            }

        }
    }

    public interface OnAvatarDialogAddListener {
        void onPrepearAvatar(String name, int price, File avatar);
    }

    public interface OnAvatarDialogUpdateListener {
        void onPrepearAvatar(String id,String name, int price, File avatar);
    }
}
