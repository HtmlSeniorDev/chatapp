package ru.readme.chatapp.dialog;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.readme.chatapp.activity.ProfileActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.object.responses.GiftResponse;
import ru.readme.chatapp.util.Network;

public class GiftDialog {

    public GiftDialog(final Fragment context, final GiftResponse giftResponse) {
        View v = LayoutInflater.from(context.getActivity()).inflate(R.layout.gift_dialog_user, null);
        TextView tvName = (TextView) v.findViewById(R.id.tv_gift_name);
        TextView tvDescription = (TextView) v.findViewById(R.id.tv_gift_description);
        TextView tvUser = (TextView) v.findViewById(R.id.tv_gift_sender);
        ImageView ivIcon = (ImageView) v.findViewById(R.id.iv_gift_element_image);
        if (giftResponse != null) {
            if (giftResponse.getDescription() != null) {
                tvDescription.setText(giftResponse.getDescription());
            }
            if (giftResponse.getName() != null) {
                tvName.setText(giftResponse.getName());
            }
            Picasso.with(context.getActivity()).load(Network.giftLink(giftResponse.getId()))
                    .error(R.drawable.ic_card_giftcard_deep_orange_700_48dp)
                    .into(ivIcon);
            if (giftResponse.getFromuser() != null) {
                tvUser.setText(giftResponse.getFromuser().getNic());
                tvUser.setTextColor(giftResponse.getFromuser().getColor());
                tvUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context.getActivity(), ProfileActivity.class);
                        intent.putExtra("id", giftResponse.getFromuser().getId());
                        context.startActivityForResult(intent, 1);
                    }
                });
            }else{
                tvUser.setText(R.string.anon);
            }
            new AlertDialog.Builder(context.getActivity())
                    .setTitle(R.string.gift)
                    .setView(v)
                    .setPositiveButton(R.string.ok, null)
                    .show();


        }
    }
}
