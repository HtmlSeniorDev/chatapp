package ru.readme.chatapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.readme.chatapp.R;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.ImageSetter;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder> {

    private List<UserResponse> elements = new ArrayList<>();
    private Context context;
    private OnUserClickListener listener;
    private boolean noAvatar = true;
    private float textSize = -1;

    public UsersAdapter(Context context, OnUserClickListener listener) {
        this.context = context;
        this.listener = listener;
    }
    public UsersAdapter(Context context, OnUserClickListener listener,boolean noAvatar) {
        this.context = context;
        this.listener = listener;
    }
    public UsersAdapter(Context context, OnUserClickListener listener, float size) {
        this.context = context;
        this.listener = listener;
        textSize = size;
    }

    public UsersAdapter(Context context, OnUserClickListener listener, boolean noAvatar, float size) {
        this.context = context;
        this.listener = listener;
        textSize = size;
    }

    public void add(UserResponse rr) {
        boolean add = true;
        for (UserResponse u : elements) {
            if (u.getId().equals(rr.getId())) {
                add = false;
                break;
            }
        }
        if (add) {
            elements.add(rr);
            notifyItemInserted(elements.size() - 1);
        }
    }

    public void delete(UserResponse pr) {
        if (pr != null) {
            for (int i = 0; i < elements.size(); i++) {
                Object o = elements.get(i);
                if (o instanceof UserResponse && ((UserResponse) o).getId().equals(pr.getId())) {
                    elements.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }
    }

    public List<UserResponse> getElements() {
        return elements;
    }

    public void setElements(List<UserResponse> users) {
        for (UserResponse user : users) {
            add(user);
        }
        for (int i = 0; i < elements.size(); i++) {
            UserResponse r = elements.get(i);
            boolean delete = true;
            for (UserResponse u : users) {
                if (u.getId().equals(r.getId())) {
                    delete = false;
                    break;
                }
            }
            if (delete) {
                elements.remove(i);
                notifyItemRemoved(i);
            }
        }
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
        final UserResponse user = elements.get(position);
        holder.tvNic.setText(user.getNic());
        holder.tvNic.setTextColor(user.getColor());
        if (textSize > 0) {
            holder.tvNic.setTextSize(textSize);
        }
        if (!noAvatar) {
            try {
                if (user.getAvatarLink() != null && user.getAvatarLink().length() > 0) {
                    ImageSetter.setAvatar(user.getAvatarLink(), context, holder.ivPhoto);
                }

            } catch (Exception e) {
                Log.e("App", "error", e);
            }
        }
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            }
        });
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listener != null) {
                    listener.onUserLongClick(user);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public interface OnUserClickListener {
        void onUserClick(UserResponse user);

        void onUserLongClick(UserResponse user);
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

