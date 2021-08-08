package ru.readme.chatapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import ru.readme.chatapp.R;
import ru.readme.chatapp.object.responses.CategoryResponse;
import ru.readme.chatapp.object.responses.PMChatResponse;
import ru.readme.chatapp.object.responses.RoomResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.ImageSetter;
import ru.readme.chatapp.util.SettingsHelper;

public class ChatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ROOM = 2;
    private static final int TYPE_CATEGORY = 1;
    private static final int TYPE_PERSONAL_CHAT = 3;
    private float textSize = 1;
    private Context context;
    private OnChatsClickListener listener;
    private List<Object> elements = new ArrayList<>();
    private String meId = "";
    private SettingsHelper settingsHelper;

    public ChatsAdapter(Context context, OnChatsClickListener listener) {
        this.context = context;
        this.listener = listener;
        settingsHelper = new SettingsHelper(context);
        meId = settingsHelper.getUserId();
        textSize = (float) settingsHelper.getRoomsSize() * context.getResources().getDimension(R.dimen.one_sp);

    }

    public void add(CategoryResponse cr) {
        elements.add(cr);
        notifyItemInserted(elements.size() - 1);
    }

    public void add(RoomResponse rr) {
        elements.add(rr);
        notifyItemInserted(elements.size() - 1);
    }

    public void add(PMChatResponse rr) {
        elements.add(rr);
        notifyItemInserted(elements.size() - 1);
    }

    public void clear() {
        int size = elements.size();
        elements = new ArrayList<>();
        notifyItemRangeRemoved(0, size);
    }

    public Object getFirstElement() {
        if (elements.size() > 0) {
            return elements.get(0);
        } else {
            return null;
        }
    }

    public void deleteElement(String id) {
        if (id == null) {
            return;
        }
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i) instanceof PMChatResponse) {
                if (((PMChatResponse) elements.get(i)).getId().equals(id)) {
                    elements.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CATEGORY) {
            View v = LayoutInflater.from(context).inflate(R.layout.category_element, null);
            v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new CategoryHolder(v);
        } else if (viewType == TYPE_ROOM) {
            View v = LayoutInflater.from(context).inflate(R.layout.chat_element, null);
            v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RoomHolder(v);
        } else if (viewType == TYPE_PERSONAL_CHAT) {
            View v = LayoutInflater.from(context).inflate(R.layout.chat_pm_element, null);
            v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RoomHolder(v);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object o = elements.get(position);
        if (getItemViewType(position) == TYPE_CATEGORY) {
            final CategoryResponse cr = (CategoryResponse) o;
            ((CategoryHolder) holder).tvName.setText(cr.getName());
            ((CategoryHolder) holder).tvName.setTextSize(textSize);
            ((CategoryHolder) holder).v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onCategoryClick(cr);
                    }
                }
            });
        } else if (getItemViewType(position) == TYPE_ROOM) {
            final RoomResponse rr = (RoomResponse) o;
            Log.e("App", "People count=" + rr.getPeopleCount());
            ((RoomHolder) holder).tvUsers.setText(rr.getPeopleCount() + "");
            ((RoomHolder) holder).tvUsers.setTextSize(textSize);
            ((RoomHolder) holder).tvName.setTextSize(textSize);
            ((RoomHolder) holder).tvName.setText(rr.getName());
            ((RoomHolder) holder).v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onRoomClick(rr);
                    }
                }
            });
        } else if (getItemViewType(position) == TYPE_PERSONAL_CHAT) {
            final PMChatResponse rr = (PMChatResponse) o;
            ((RoomHolder) holder).tvName.setTextSize(textSize);
            ((RoomHolder) holder).v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onPersonalChatClickl(rr);
                    }
                }
            });

            ((RoomHolder) holder).v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        listener.onPersonalChatLongClickl(rr);
                    }
                    return false;
                }
            });
            String name = "";

            for (UserResponse user : rr.getUsers()) {
                if (!user.getId().equals(meId)) {
                    name += "<font color='#" + Integer.toHexString(user.getColor()) + "'>" + user.getNic() + "</font>, ";
                }
            }
            if (name.length() > 2) {
                name = name.substring(0, name.length() - 2);
            }
            if (rr.getUnreaded() > 0) {
                name += "   <font color=red>" + rr.getUnreaded() + "</font>";
            }
            ((RoomHolder) holder).tvName.setText(Html.fromHtml(name));
            if (rr.getLastMessage() != null) {
                ((RoomHolder) holder).tvMessage.setText(rr.getLastMessage().getMessage());
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                ((RoomHolder) holder).tvDate.setText(sdf.format(rr.getLastMessage().getCreatedAt()));
                if (rr.getLastMessage().getUser() != null) {
                    ((RoomHolder) holder).tvUser.setText(rr.getLastMessage().getUser().getNic());
                    ((RoomHolder) holder).tvUser.setTextColor(rr.getLastMessage().getUser().getColor());
                    try {
                        if (rr.getLastMessage().getUser().getAvatarLink() != null && rr.getLastMessage().getUser().getAvatarLink().length() > 0) {

                            ImageSetter.setAvatar(rr.getLastMessage().getUser().getAvatarLink(), context, ((RoomHolder) holder).ivPhoto);
                        }

                    } catch (Exception e) {
                    }
                } else {
                    ((RoomHolder) holder).tvUser.setText("-");
                }
            } else {
                ((RoomHolder) holder).tvDate.setText("-");
                ((RoomHolder) holder).tvUser.setText("-");
                ((RoomHolder) holder).tvMessage.setText("-");
            }
        }
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object o = elements.get(position);
        if (o instanceof CategoryResponse) {
            return TYPE_CATEGORY;
        } else if (o instanceof RoomResponse) {
            return TYPE_ROOM;
        } else if (o instanceof PMChatResponse) {
            return TYPE_PERSONAL_CHAT;
        } else {
            return 0;
        }
    }

    public interface OnChatsClickListener {
        void onRoomClick(RoomResponse room);

        void onPersonalChatClickl(PMChatResponse chat);

        void onPersonalChatLongClickl(PMChatResponse chat);

        void onCategoryClick(CategoryResponse category);
    }

    public static class CategoryHolder extends RecyclerView.ViewHolder {

        View v;
        TextView tvName;

        public CategoryHolder(View itemView) {
            super(itemView);
            v = itemView;
            tvName = (TextView) v.findViewById(R.id.tv_category_element_name);
        }
    }

    public static class RoomHolder extends RecyclerView.ViewHolder {

        View v;
        EmojiconTextView tvMessage;
        TextView tvName, tvUser, tvDate, tvUsers;
        ImageView ivPhoto;

        public RoomHolder(View itemView) {
            super(itemView);
            v = itemView;
            ivPhoto = (ImageView) v.findViewById(R.id.iv_chat_element_photo);
            tvDate = (TextView) v.findViewById(R.id.tv_chat_element_date);
            tvMessage = (EmojiconTextView) v.findViewById(R.id.tv_chat_element_message);
            tvName = (TextView) v.findViewById(R.id.tv_chat_element_name);
            tvUser = (TextView) v.findViewById(R.id.tv_chat_element_user_name);
            tvUsers = (TextView) v.findViewById(R.id.tv_chat_element_people_count);
        }
    }
}
