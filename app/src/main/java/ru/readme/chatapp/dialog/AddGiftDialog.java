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
import ru.readme.chatapp.R;
import ru.readme.chatapp.object.responses.GiftResponse;
import ru.readme.chatapp.util.Network;

public class AddGiftDialog implements View.OnClickListener {

    Fragment context;
    private OnGiftDialogAddListener listener;
    private EditText edName, edPrice, edDescription;
    private Button btPhoto;
    private ImageView ivPhoto;
    private File f = null;

    public AddGiftDialog(Fragment context, final OnGiftDialogAddListener listener) {
        this.context = context;
        this.listener = listener;
        View v = LayoutInflater.from(context.getActivity()).inflate(R.layout.add_gift_dialog, null);
        edName = (EditText) v.findViewById(R.id.ed_add_gift_name);
        edPrice = (EditText) v.findViewById(R.id.ed_add_gift_price);
        ivPhoto = (ImageView) v.findViewById(R.id.iv_add_gift_avatar);
        btPhoto = (Button) v.findViewById(R.id.bt_add_gift_select_photo);
        edDescription = (EditText) v.findViewById(R.id.ed_add_gift_description);
        btPhoto.setOnClickListener(this);
        new AlertDialog.Builder(context.getActivity())
                .setTitle(R.string.add_avatar)
                .setView(v)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = edName.getText().toString();
                        String pr = edPrice.getText().toString();
                        String description = edDescription.getText().toString();
                        int price = 0;
                        try {
                            price = Integer.parseInt(pr) * 100;
                        } catch (Exception e) {

                        }
                        if (listener != null) {
                            listener.onPrepearGift(name, description, price, f);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public AddGiftDialog(Fragment context, final OnGiftDialogUpdateListener listener, final GiftResponse gift) {
        this.context = context;
        final OnGiftDialogUpdateListener l = listener;
        View v = LayoutInflater.from(context.getActivity()).inflate(R.layout.add_gift_dialog, null);
        edName = (EditText) v.findViewById(R.id.ed_add_gift_name);
        edPrice = (EditText) v.findViewById(R.id.ed_add_gift_price);
        edDescription = (EditText) v.findViewById(R.id.ed_add_gift_description);
        ivPhoto = (ImageView) v.findViewById(R.id.iv_add_gift_avatar);
        edPrice.setText((int) (gift.getPrice() / 100) + "");
        edName.setText(gift.getName());
        if (gift.getDescription() != null) {
            edDescription.setText(gift.getDescription());
        }
        Picasso.with(context.getActivity())
                .load(Network.giftLink(gift.getId()))
                .error(R.drawable.ic_card_giftcard_deep_orange_700_48dp)
                .into(ivPhoto);
        btPhoto = (Button) v.findViewById(R.id.bt_add_gift_select_photo);
        btPhoto.setOnClickListener(this);
        new AlertDialog.Builder(context.getActivity())
                .setTitle(R.string.update_avatar)
                .setView(v)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = edName.getText().toString();
                        String pr = edPrice.getText().toString();
                        String description = edDescription.getText().toString();
                        int price = 0;
                        try {
                            price = Integer.parseInt(pr) * 100;
                        } catch (Exception e) {

                        }
                        if (l != null) {
                            l.onPrepearGift(gift.getId(), name, description, price, f);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_add_gift_select_photo) {
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

    public interface OnGiftDialogAddListener {
        void onPrepearGift(String name, String description, int price, File gift);
    }

    public interface OnGiftDialogUpdateListener {
        void onPrepearGift(String id, String name, String description, int price, File gift);
    }
}
