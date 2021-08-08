package ru.readme.chatapp.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import ru.readme.chatapp.activity.App;
import ru.readme.chatapp.R;

public class ImageSetter {

    public static void setAvatar(String id, Context context, ImageView view) {
        try {
            if (new File(App.avasPath + id).exists()) {
                view.setImageURI(Uri.fromFile(new File(App.avasPath + id)));
            } else {
                Picasso.with(context).load(Network.avatarLink(id))
                        .error(R.drawable.ic_portrait_deep_orange_700_48dp)
                        .into(view);
            }
        } catch (Exception ee) {
            Picasso.with(context).load(Network.avatarLink(id))
                    .error(R.drawable.ic_portrait_deep_orange_700_48dp)
                    .into(view);
        }
    }

    public static InputStream getAvatarStream(String id, Context context) {
        try {
           File f = new File(App.avasPath + id);
            if(f.exists()){
                return  new FileInputStream(f);
            }else{
                return null;
            }
        } catch (Exception e) {
            Log.e("Chat Error", "get avatar", e);
        }
        return  null;
    }

    public static boolean hasAvatar(String id) {
        try {
            File f = new File(App.avasPath + id);
            if(f.exists()){
                return  true;
            }else{
                return false;
            }
        } catch (Exception e) {
            Log.e("Chat Error", "get avatar", e);
        }
        return  false;
    }
}
