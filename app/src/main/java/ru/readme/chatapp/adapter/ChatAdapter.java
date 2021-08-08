package ru.readme.chatapp.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import ru.readme.chatapp.activity.App;
import ru.readme.chatapp.activity.ImageViewActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.object.MessageType;
import ru.readme.chatapp.object.responses.AttachmentResponse;
import ru.readme.chatapp.object.responses.MessageResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.ImageSetter;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.SettingsHelper;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageHolder> {

    private List<MessageResponse> elements = new ArrayList<>();
    private Fragment context;
    private OnChatMessageClickListener listener;
    private int type;
    private String userId;
    private SettingsHelper settingsHelper;
    private float textSize = 1;
    private float avasSize = 1;
    private String myNic = null;
    private int type_all = 1;
    private int type_pm_me = 2;
    private int type_pm_another = 3;

    public ChatAdapter(Fragment context, OnChatMessageClickListener listener, int type) {
        settingsHelper = new SettingsHelper(context.getActivity());
        this.context = context;
        this.listener = listener;
        this.type = type;
        myNic=settingsHelper.getUserNic();
        userId = settingsHelper.getUserId();
        textSize =(float) settingsHelper.getTextSize()*context.getActivity().getResources().getDimension(R.dimen.one_sp);
        avasSize = (float)settingsHelper.getAvasSize()*context.getActivity().getResources().getDimension(R.dimen.one_sp);
    }


    public void addElement(MessageResponse messageResponse) {
        if (messageResponse != null) {
            boolean add = true;
            int pos = 0;
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).getId().equals(messageResponse.getId())) {
                    pos = i;
                    add = false;
                    break;
                }
            }
            if (add) {
                elements.add(messageResponse);
                notifyItemInserted(pos + 1);
                Collections.sort(elements, new Comparator<MessageResponse>() {
                    @Override
                    public int compare(MessageResponse m1, MessageResponse m2) {

                        Long l1 = m1.getCreatedAt().getTime();
                        Long l2 = m2.getCreatedAt().getTime();
                        if (l1 > l2) {
                            return 1;
                        } else if (l2 > l1) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });
                notifyDataSetChanged();
            } else {
                elements.set(pos, messageResponse);
                notifyItemChanged(pos + 1);
            }
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<MessageResponse> getElements() {
        return elements;
    }

    public MessageResponse getLastElement() {
        if (elements.size() > 0) {
            return elements.get(elements.size() - 1);
        } else {
            return null;
        }
    }

    public MessageResponse getFirstElement() {
        if (elements.size() > 0) {
            return elements.get(0);
        } else {
            return null;
        }
    }


    public void clear() {
        int size = elements.size();
        elements = new ArrayList<>();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemViewType(int position) {
        if (type == MessageType.TYPE_CHAT) {
            return type_all;
        } else if (elements.get(position).getUser().getId().equals(userId)) {
            return type_pm_me;
        } else {
            return type_pm_another;
        }
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == type_all) {
            v = (LayoutInflater.from(context.getActivity()).inflate(R.layout.chat_message_element, null));
        } else if (viewType == type_pm_another) {
            v = (LayoutInflater.from(context.getActivity()).inflate(R.layout.chat_message_pm_element_1, null));
        } else {
            v = (LayoutInflater.from(context.getActivity()).inflate(R.layout.chat_message_pm_element_2, null));
        }
        MessageHolder mh = new MessageHolder(v);
        if(mh.tvMessage1!=null){
            mh.tvMessage1.setTextSize(textSize);
            mh.tvMessage1.setEmojiconSize((int)(textSize*1.4f));
        }
        return new MessageHolder(v);
    }

    private List<Integer> subGenImgAtt(int count) {
        List<Integer> res = new ArrayList<>();

        return res;
    }

    private List<Integer> generateImageAttachmentLayouts(int count) {
        List<Integer> result = new ArrayList<>();
        if (count == 1) {
            result.add(1);
        } else {
            while (count > 0) {
                if (count == 2) {
                    result.add(2);
                    count -= 2;
                } else if (count == 3) {
                    result.add(3);
                    count -= 3;
                } else if (count == 4) {
                    result.add(2);
                    result.add(2);
                    count -= 4;
                } else {
                    result.add(3);
                    count -= 3;
                }
            }
        }
        return result;
    }

    @Override
    public void onBindViewHolder(final MessageHolder holder, int position) {
        final MessageResponse mr = elements.get(position);
        holder.llAttachments.removeAllViews();
        if (mr.getAttachments().size() > 0) {
            View vSpace = new View(context.getActivity());
            vSpace.setLayoutParams(new ViewGroup.LayoutParams(0, context.getResources().getDimensionPixelSize(R.dimen.short_padding)));
            holder.llAttachments.addView(vSpace);
            List<AttachmentResponse> imagesAttachments = new ArrayList<>();
            List<AttachmentResponse> filesAttachments = new ArrayList<>();
            for (AttachmentResponse ar : mr.getAttachments()) {
                if (ar.getFileName().toLowerCase().indexOf(".png") > 0 || ar.getFileName().toLowerCase().indexOf("jpeg") > 0 || ar.getFileName().toLowerCase().indexOf("jpg") > 0) {
                    imagesAttachments.add(ar);
                } else {
                    filesAttachments.add(ar);
                }
            }
            if (imagesAttachments.size() > 0) {
                List<Integer> imgCounts = generateImageAttachmentLayouts(imagesAttachments.size());
                int curAtt = 0;
                for (final Integer count : imgCounts) {
                    View v = null;
                    ImageView[] ivs = new ImageView[3];
                    if (count == 1) {
                        v = LayoutInflater.from(context.getActivity()).inflate(R.layout.attachment_image_single, null);
                    } else if (count == 2) {
                        v = LayoutInflater.from(context.getActivity()).inflate(R.layout.attachment_image_double, null);
                    } else if (count == 3) {
                        v = LayoutInflater.from(context.getActivity()).inflate(R.layout.attachment_image_triple, null);
                    }
                    ivs[0] = (ImageView) v.findViewById(R.id.iv_attachment_image_first);
                    ivs[1] = (ImageView) v.findViewById(R.id.iv_attachment_image_second);
                    ivs[2] = (ImageView) v.findViewById(R.id.iv_attachment_image_therf);

                    for (int i = 0; i < count; i++) {
                        Log.e("App", count + " " + i + " " + curAtt);
                        AttachmentResponse ar = imagesAttachments.get(curAtt);
                        final String link = Network.LINK + "attachment/" + ar.getFileName();
                        Picasso.with(context.getActivity()).load(Uri.parse(link)).into(ivs[i]);
                        ivs[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent i = new Intent(context.getActivity(), ImageViewActivity.class);
                                i.putExtra("url", (link));
                                context.startActivityForResult(i, 1);
                            }
                        });
                        curAtt++;
                    }
                    v.setId(-1 * curAtt);
                    holder.llAttachments.addView(v);
                }
            }
            if (filesAttachments.size() > 0) {
                for (final AttachmentResponse ar : filesAttachments) {
                    View v = LayoutInflater.from(context.getActivity()).inflate(R.layout.attachment_file, null);
                    TextView tvFileName = (TextView) v.findViewById(R.id.tv_attachment_file_name);
                    tvFileName.setText(ar.getFileName());
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String link = Network.LINK + "attachment/" + ar.getFileName();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(link));
                            context.startActivityForResult(i, 1);
                        }
                    });
                    holder.llAttachments.addView(v);
                }
            }
        }
        if (getItemViewType(position) == type_all) {
            boolean toMe = myNic!=null && mr.getMessage().contains(myNic+",");

            if(toMe){
                holder.llContent.setBackgroundColor(context.getResources().getColor(R.color.colorToMy));
            }else{
                holder.llContent.setBackgroundResource(R.drawable.bt_transporent);
            }

            SpannableString ss=null;
            List<TextTags> bold = new ArrayList<>();

            if (mr.isHideNick() == true) {// ник зашел/вышел
                String src = mr.getMessage();
                src = tegged(src, bold, "<b>", "</b>");
                ss = new SpannableString(src);
                for (TextTags t : bold) {
                    ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), t.start, t.end, 0);
                }
                holder.tvMessage1.setText(ss);
                if(src.contains(" зашел") || src.contains(" зашла")){
                    holder.iv.setImageDrawable(context.getResources().getDrawable(R.drawable.wb));
                    android.view.ViewGroup.LayoutParams layoutParams = holder.iv.getLayoutParams();
                    layoutParams.width = (int) 0;
                    layoutParams.height = (int) 0;
                    holder.iv.setLayoutParams(layoutParams);
                }
                else if(src.contains(" вышел") || src.contains(" вышла")){
                    holder.iv.setImageDrawable(context.getResources().getDrawable(R.drawable.wb));
                    android.view.ViewGroup.LayoutParams layoutParams = holder.iv.getLayoutParams();
                    layoutParams.width = (int) 0;
                    layoutParams.height = (int) 0;
                    holder.iv.setLayoutParams(layoutParams);
                }
            } else if (mr.isHideNick() == false) {//ник не зашел вышел
                String src = mr.getUser().getNic() + ": " + mr.getMessage();
                if(mr.isSystem() == true) {
                    src = "" + mr.getMessage();
                    holder.llContent.setBackgroundColor(context.getResources().getColor(R.color.colorSystem));
                    holder.iv.setImageDrawable(context.getResources().getDrawable(R.drawable.wb));
                    android.view.ViewGroup.LayoutParams layoutParams = holder.iv.getLayoutParams();
                    layoutParams.width = (int) 0;
                    layoutParams.height = (int) 0;
                    holder.iv.setLayoutParams(layoutParams);

                }
                if(mr.isSystem() == false) {
                    if (mr.getUser().getAvatarLink() != null) {
                        boolean hasAvatar = ImageSetter.hasAvatar(mr.getUser().getAvatarLink());
                        if (hasAvatar) {
                            Bitmap bmp = BitmapFactory.decodeFile(App.avasPath + mr.getUser().getAvatarLink());
                            holder.iv.setImageBitmap(bmp);
                            android.view.ViewGroup.LayoutParams layoutParams = holder.iv.getLayoutParams();
                            layoutParams.width = (int) avasSize;
                            layoutParams.height = (int) avasSize;
                            holder.iv.setLayoutParams(layoutParams);
                        }
                    } else if (mr.getUser().getAvatarLink() == null) {
                        holder.iv.setImageDrawable(context.getResources().getDrawable(R.drawable.wb));
                        android.view.ViewGroup.LayoutParams layoutParams = holder.iv.getLayoutParams();
                        layoutParams.width = (int) 0;
                        layoutParams.height = (int) 0;
                        holder.iv.setLayoutParams(layoutParams);
                    }
                }
                holder.tvMessage1.setText(src);
            }
            if(mr.isHideNick()==true || mr.isSystem()==true) {
              holder.tvMessage1.setTextColor(Color.BLACK);//цвет сообщения
            } else{
                  holder.tvMessage1.setTextColor(mr.getUser().getColor());//цвет сообщения
                }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm:ss");
            holder.tvMessage1.setText(sdf.format(mr.getCreatedAt()) + "\n" + mr.getMessage());
        }
        holder.tvMessage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    if(/*mr.isHideNick() == false &&*/ mr.isSystem() == false) {
                        listener.onMessageUserClick(mr.getUser());
                    }
                }
            }
        });
        holder.tvMessage1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listener != null) {
                    listener.onMessageUserLongClick(mr.getUser());
                }
                return false;
            }
        });
    }

    private String tegged(String src, List<TextTags> list, String startTag, String endTag) {
        list.clear();
        while (true) {
            int start = src.indexOf(startTag);
            if (start < 0) {
                break;
            }
            src = src.substring(0, start) + src.substring(start + startTag.length());
            int end = src.indexOf(endTag);
            if (end < 0) {
                break;
            }
            src = src.substring(0, end) + src.substring(end + endTag.length());
            TextTags t = new TextTags();
            t.start = start;
            t.end = end;
            list.add(t);
        }
        return src;
    }


    @Override
    public int getItemCount() {
        return elements.size();
    }

    public interface OnChatMessageClickListener {
        void onMessageClick(MessageResponse messageResponse);
        void onMessageUserClick(UserResponse user);
        void onMessageUserLongClick(UserResponse user);
        void onAddNew(MessageResponse ms);
    }

    public static class TextTags {
        int start = 0;
        int end = 0;
    }

    public static class MessageHolder extends RecyclerView.ViewHolder {
        LinearLayout llContent, llAttachments;
        EmojiconTextView tvMessage1;
        ImageView iv;
        View v;

        public MessageHolder(View itemView) {
            super(itemView);
            v = itemView;
            llContent = (LinearLayout) v.findViewById(R.id.ll_chat_message_element_content);
            llAttachments = (LinearLayout) v.findViewById(R.id.ll_chat_message_element_attachments);
            tvMessage1 = (EmojiconTextView) v.findViewById(R.id.tv_chat_message_element_message_1);
            iv = (ImageView) v.findViewById(R.id.avs);
            tvMessage1.setEmojiconSize(tvMessage1.getContext().getResources().getDimensionPixelOffset(R.dimen.emojy_height_icon));
        }
    }

    public static class MySpan extends ClickableSpan {
        private int color;

        private View.OnClickListener listener;

        public MySpan(int color, View.OnClickListener listener) {
            this.color = color;
            this.listener = listener;
        }

        @Override
        public void onClick(View widget) {
            if (listener != null) {
                listener.onClick(widget);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.linkColor = color;
            ds.setUnderlineText(false);
            super.updateDrawState(ds);
        }
    }
}
