package ru.readme.chatapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import ru.readme.chatapp.R;
import ru.readme.chatapp.object.responses.GiftResponse;
import ru.readme.chatapp.util.Network;

public class GiftsAdapter extends RecyclerView.Adapter<GiftsAdapter.GiftHolder> {
    private Context context;
    private List<GiftResponse> elements = new ArrayList<>();
    private OnGiftElementClickListener listener;
    private boolean shrt = false;

    public GiftsAdapter(Context context, OnGiftElementClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public GiftsAdapter(Context context, OnGiftElementClickListener listener, boolean shrt) {
        this.context = context;
        this.shrt = shrt;
        this.listener = listener;
    }

    @Override
    public GiftHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (!shrt) {
            v = LayoutInflater.from(context).inflate(R.layout.gift_element, null);
            v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.gift_element_short, null);
            v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.MATCH_PARENT));
        }
        return new GiftHolder(v);
    }

    @Override
    public void onBindViewHolder(final GiftHolder holder, final int position) {
        final GiftResponse ar = elements.get(position);
        if (!shrt) {
            int price = ar.getPrice() / 100;
            float pr = price - (float) ar.getPrice() / 100;
            if (pr < 0) {
                pr = 1f - pr;
            }
            String spr = pr + "000";
            spr = spr.substring(2, 4);
            spr = price + "," + spr +  " руб.";
            holder.tvPrice.setText(spr);
            holder.tvDescription.setText(ar.getName());
        }
        Picasso.with(context).load(Network.giftLink(ar.getId()))
                .error(R.drawable.ic_card_giftcard_deep_orange_700_48dp)
                .into(holder.ivAvatar);
        if (listener != null) {
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onGiftElementClick(ar);
                }
            });
        }
    }

    public List<GiftResponse> getElements() {
        return elements;
    }

    public void add(GiftResponse avatar) {
        if (avatar != null) {
            if(shrt){
                elements.add(avatar);
                notifyItemInserted(elements.size() - 1);
                return;
            }
            int pos = -1;
            for (int i = 0; i < elements.size(); i++) {
                GiftResponse test = elements.get(i);
                if (test.getId().equals(avatar.getId())) {
                    pos = i;
                    break;
                }
            }
            if (pos > -1) {
                elements.set(pos, avatar);
                notifyItemChanged(pos);
                notifyDataSetChanged();
            } else {
                elements.add(avatar);
                notifyItemInserted(elements.size() - 1);
            }
        }
    }

    public void remove(GiftResponse avatar) {
        if (avatar != null) {
            int pos = -1;
            for (int i = 0; i < elements.size(); i++) {
                GiftResponse test = elements.get(i);
                if (test.getId().equals(avatar.getId())) {
                    pos = i;
                    break;
                }
            }
            if (pos > -1) {
                elements.remove(pos);
                notifyItemRemoved(pos);
            }
        }
    }


    public void clear() {
        int size = elements.size();
        elements = new ArrayList<>();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public interface OnGiftElementClickListener {
        void onGiftElementClick(GiftResponse avatarResponse);
    }

    public static class GiftHolder extends RecyclerView.ViewHolder {

        View v;
        TextView tvDescription, tvPrice;
        ImageView ivAvatar;

        public GiftHolder(View view) {
            super(view);
            v = view;
            tvDescription = (TextView) v.findViewById(R.id.tv_gift_description);
            tvPrice = (TextView) v.findViewById(R.id.tv_gift_price);
            ivAvatar = (ImageView) v.findViewById(R.id.iv_gift_element_image);
        }
    }
}
