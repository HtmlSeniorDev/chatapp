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
import ru.readme.chatapp.object.responses.FolderResponse;
import ru.readme.chatapp.object.responses.PhotoResponse;
import ru.readme.chatapp.util.Network;

public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_FOLDER = 1;
    private final static int TYPE_PHOTO = 2;
    private List<Object> elements = new ArrayList<>();
    private Context context;
    private OnPhotoClickListener listener;
    private boolean me;
    private boolean fullScreen = false;

    public PhotosAdapter(Context context, OnPhotoClickListener listener, boolean me) {
        elements = new ArrayList<>();
        this.context = context;
        this.listener = listener;
        this.me = me;
        if (me) {
            elements.add(null);
        }
    }

    public PhotosAdapter(Context context, OnPhotoClickListener listener, boolean me, boolean fullscreen) {
        elements = new ArrayList<>();
        this.context = context;
        this.listener = listener;
        this.me = me;
        this.fullScreen = fullscreen;
        if (me) {
            elements.add(null);
        }
    }

    public void add(PhotoResponse pr) {
        if (pr != null) {
            elements.add(pr);
            notifyItemInserted(elements.size() - 1);
        }
    }

    public void add(FolderResponse pr) {
        if (pr != null) {
            elements.add(pr);
            notifyItemInserted(elements.size() - 1);
        }
    }

    public void delete(PhotoResponse pr) {
        if (pr != null) {
            for (int i = 0; i < elements.size(); i++) {
                Object o = elements.get(i);
                if (o instanceof PhotoResponse && ((PhotoResponse) o).getId().equals(pr.getId())) {
                    elements.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == TYPE_PHOTO) {
            v = LayoutInflater.from(context).inflate(R.layout.photo_element, null);
            if (!fullScreen) {
                v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.MATCH_PARENT));
            } else {
                v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            }
            return new PhotoHolder(v);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.folder_element, null);
            if(!fullScreen) {
                v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.MATCH_PARENT));
            }else{
                v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            }
            return new PhotoFolderHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PHOTO) {
            final PhotoResponse pr = (PhotoResponse) elements.get(position);
            if (pr != null) {
                Picasso.with(context)
                        .load(Network.photoLink(pr.getId()))
                        .error(R.drawable.ic_block_grey_700_48dp)
                        .into(((PhotoHolder) holder).iv);
                ((PhotoHolder) holder).iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onPhotoClick(pr);
                        }
                    }
                });
                ((PhotoHolder) holder).iv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (listener != null) {
                            listener.onLongPhotoClick(pr);
                        }
                        return true;
                    }
                });
            } else if (me) {
                ((PhotoHolder) holder).iv.setImageResource(R.drawable.ic_add_a_photo_grey_700_48dp);
                ((PhotoHolder) holder).iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onPhotoClick(null);
                        }
                    }
                });
                ((PhotoHolder) holder).iv.setOnLongClickListener(null);
            } else {
                ((PhotoHolder) holder).iv.setImageResource(R.drawable.ic_block_grey_700_48dp);
            }
        } else {
            final FolderResponse folder = (FolderResponse) elements.get(position);
            ((PhotoFolderHolder) holder).name.setText(folder.getName());
            ((PhotoFolderHolder) holder).v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onFolderClick(folder);
                    }
                }
            });
            ((PhotoFolderHolder) holder).v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null) {
                        listener.onFolderLongClick(folder);
                    }
                    return true;
                }
            });
        }
    }

    public List<Object> getElements() {
        return elements;
    }

    public List<PhotoResponse> getPhotos() {
        List<PhotoResponse> photos = new ArrayList<>();
        for (Object o : elements) {
            if (o != null && o instanceof PhotoResponse) {
                photos.add((PhotoResponse) o);
            }
        }
        return photos;
    }

    public List<FolderResponse> getFolders() {
        List<FolderResponse> folders = new ArrayList<>();
        for (Object o : elements) {
            if (o != null && o instanceof FolderResponse) {
                folders.add((FolderResponse) o);
            }
        }
        return folders;
    }

    @Override
    public int getItemViewType(int position) {
        if (elements.get(position) == null) {
            return TYPE_PHOTO;
        } else if (elements.get(position) instanceof PhotoResponse) {
            return TYPE_PHOTO;
        } else if (elements.get(position) instanceof FolderResponse) {
            return TYPE_FOLDER;
        } else {
            return -1;
        }
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(PhotoResponse photoResponse);

        void onFolderClick(FolderResponse folderResponse);

        void onFolderLongClick(FolderResponse folderResponse);

        void onLongPhotoClick(PhotoResponse photoResponse);
    }


    public static class PhotoHolder extends RecyclerView.ViewHolder {

        private View v;
        private ImageView iv;

        public PhotoHolder(View itemView) {
            super(itemView);
            v = itemView;
            iv = (ImageView) itemView.findViewById(R.id.iv_image_element_image);
        }
    }

    public static class PhotoFolderHolder extends RecyclerView.ViewHolder {

        private View v;
        private TextView name;

        public PhotoFolderHolder(View itemView) {
            super(itemView);
            v = itemView;
            name = (TextView) v.findViewById(R.id.tv_folder_element_name);
        }
    }
}
