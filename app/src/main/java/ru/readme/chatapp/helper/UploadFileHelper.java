package ru.readme.chatapp.helper;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import ru.readme.chatapp.util.MultipartUtility;

/**
 * Created by dima on 27.11.16.
 */

public class UploadFileHelper extends AsyncTask<UploadFileHelper.Params, Void, Boolean> {

    private OnUploadFileResultListener listener;

    public UploadFileHelper(OnUploadFileResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Params... paramses) {
        Params par = null;
        if (paramses != null && paramses.length > 0 && paramses[0] != null) {
            par = paramses[0];
            try {
               boolean resp = MultipartUtility.upload(par.file,par.token,par.id);
                return resp;
            } catch (Exception e) {
                Log.e("App", "send file error", e);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (listener != null) {
            listener.onResult(aBoolean);
        }
    }

    public interface OnUploadFileResultListener {
        void onResult(boolean result);
    }

    public static class Params {
        File file;
        String id;
        String token;

        public Params(File file, String id, String token) {
            this.file = file;
            this.id = id;
            this.token = token;
        }
    }
}
