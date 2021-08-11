package ru.readme.chatapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.readme.chatapp.R;
import ru.readme.chatapp.object.responses.AvatarResponse;
import ru.readme.chatapp.util.ImageSetter;

public class AvatarsAdapter extends RecyclerView.Adapter<AvatarsAdapter.AvatarHolder> {

    private Context context;
    private List<AvatarResponse> elements = new ArrayList<>();
    private OnAvatarElementClickListener listener;

    public AvatarsAdapter(Context context, OnAvatarElementClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public AvatarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.avatar_element, null);
        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new AvatarHolder(v);
    }

    @Override
    public void onBindViewHolder(final AvatarHolder holder, final int position) {
        final AvatarResponse ar = elements.get(position);
        int price = ar.getPrice() / 100;
        float pr = price - (float) ar.getPrice() / 100;
        if (pr < 0) {
            pr = 1f - pr;
        }
        String spr = pr + "000";
        spr = spr.substring(2, 4);
        spr = price + "," + spr + " руб.";
        holder.tvPrice.setText(spr);
        holder.tvDescription.setText(ar.getName());
        ImageSetter.setAvatar(ar.getId(),context,holder.ivAvatar);
        if (listener != null) {
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onAvatarElementClick(ar);
                }
            });
        }
    }

    public List<AvatarResponse> getElements() {
        return elements;
    }

    public void add(AvatarResponse avatar) {
        if (avatar != null) {
            int pos = -1;
            for (int i = 0; i < elements.size(); i++) {
                AvatarResponse test = elements.get(i);
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

    public void remove(AvatarResponse avatar) {
        if (avatar != null) {
            int pos = -1;
            for (int i = 0; i < elements.size(); i++) {
                AvatarResponse test = elements.get(i);
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

    public interface OnAvatarElementClickListener {
        void onAvatarElementClick(AvatarResponse avatarResponse);
    }

    public static class AvatarHolder extends RecyclerView.ViewHolder {

        View v;
        TextView tvDescription, tvPrice;
        ImageView ivAvatar;

        public AvatarHolder(View view) {
            super(view);
            v = view;
            tvDescription = (TextView) v.findViewById(R.id.tv_avatar_description);
            tvPrice = (TextView) v.findViewById(R.id.tv_avatar_price);
            ivAvatar = (ImageView) v.findViewById(R.id.iv_avatar_element_image);
        }
    }
}
