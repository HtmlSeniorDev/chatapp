package ru.readme.chatapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ru.readme.chatapp.R;
import ru.readme.chatapp.object.responses.AttachmentResponse;

public class AddAttachmentsAdapter extends RecyclerView.Adapter<AddAttachmentsAdapter.AttachmentHolder> {

    private Context context;
    private OnAttachDeleteListener listener;
    private List<AttachmentResponse> elements = new ArrayList<>();

    public AddAttachmentsAdapter(Context context, OnAttachDeleteListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public AttachmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.attachment_element, null);
        return new AttachmentHolder(v);
    }

    @Override
    public void onBindViewHolder(AttachmentHolder holder, final int position) {
        final AttachmentResponse ar = elements.get(position);
        File f = new File(ar.getFileName());
        if (f.getName().indexOf(".png") > 0 || f.getName().indexOf(".jpeg") > 0) {
            holder.ivIcon.setVisibility(View.INVISIBLE);
            holder.tvName.setVisibility(View.INVISIBLE);
            holder.ivPhoto.setVisibility(View.VISIBLE);
            Log.e("App", f.toString());
            holder.ivPhoto.setImageURI(Uri.parse(f.toString()));
        } else {
            holder.ivIcon.setVisibility(View.VISIBLE);
            holder.tvName.setVisibility(View.VISIBLE);
            holder.ivPhoto.setVisibility(View.INVISIBLE);
        }
        holder.tvName.setText(f.getName());
        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onAttachmentDelete(ar, position);
                }
            }
        });

    }

    public void deleteElement(int pos) {
        elements.remove(pos);
        notifyItemRemoved(pos);
    }

    public List<AttachmentResponse> getElements() {
        return elements;
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void clear() {
        int size = elements.size();
        elements = new ArrayList<>();
        notifyItemRangeRemoved(0, size);
    }

    public void add(AttachmentResponse rr) {
        elements.add(rr);
        notifyItemInserted(elements.size() - 1);
    }


    public interface OnAttachDeleteListener {
        void onAttachmentDelete(AttachmentResponse ar, int pos);
    }

    public class AttachmentHolder extends RecyclerView.ViewHolder {
        View v;
        ImageButton ibDelete;
        ImageView ivIcon, ivPhoto;
        TextView tvName;

        public AttachmentHolder(View itemView) {
            super(itemView);
            v = itemView;
            ibDelete = (ImageButton) v.findViewById(R.id.ib_attachment_element_delete);
            tvName = (TextView) v.findViewById(R.id.tv_attachment_element_name);
            ivIcon = (ImageView) v.findViewById(R.id.iv_attachment_element_icon);
            ivPhoto = (ImageView) v.findViewById(R.id.iv_attachment_element_bg);
        }
    }
}
