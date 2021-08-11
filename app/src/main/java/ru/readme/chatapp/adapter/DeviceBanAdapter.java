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

public class DeviceBanAdapter extends RecyclerView.Adapter<DeviceBanAdapter.UserHolder> {
    private List<String> elements = new ArrayList<>();
    private Context context;
    private OnDeviceClickListener listener;
    private boolean noAvatar = true;

    public DeviceBanAdapter(Context context, OnDeviceClickListener listener) {
        this.context = context;
        this.listener = listener;
    }


    public void add(String rr) {
        boolean add = true;
        for (String u : elements) {
            if (rr.equals(u)) {
                add = false;
                break;
            }
        }
        if (add) {
            elements.add(rr);
            notifyItemInserted(elements.size() - 1);
        }
    }

    public void delete(String pr) {
        if (pr != null) {
            for (int i = 0; i < elements.size(); i++) {
                String o = elements.get(i);
                if (o.equals(pr)) {
                    elements.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }
    }

    public List<String> getElements() {
        return elements;
    }

    public void clear() {
        int size = elements.size();
        elements = new ArrayList<>();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (!noAvatar) {
            v = LayoutInflater.from(context).inflate(R.layout.user_element, null);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.user_element_short, null);
        }
        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new UserHolder(v);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        final String user = elements.get(position);
        holder.tvNic.setText(user);
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onDeviceClick(user);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return elements.size();
    }
    public interface OnDeviceClickListener {
        void onDeviceClick(String devId);
    }

    public class UserHolder extends RecyclerView.ViewHolder {
        View v;
        ImageView ivPhoto;
        TextView tvNic;

        public UserHolder(View itemView) {
            super(itemView);
            v = itemView;
            ivPhoto = (ImageView) v.findViewById(R.id.iv_user_element_icon);
            tvNic = (TextView) v.findViewById(R.id.tv_user_element_nic);
        }
    }

}

