package ru.readme.chatapp.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.activity.App;
import ru.readme.chatapp.object.responses.AvatarResponse;
import ru.readme.chatapp.object.responses.AvatarsLastUpdateResponse;
import ru.readme.chatapp.object.responses.AvatarsResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.SettingsHelper;

/**
 * Created by dima on 21.12.16.
 */

public class AvatarsLoader {

    private Context context;

    public AvatarsLoader(Context context) {
        this.context = context;
    }

    public static void load(final String id, final OnAvatarLoadListener listener) {

        //загрузим аву
        final File dir = new File(App.avasPath);
        Call<ResponseBody> avatarbytes = Network.getChatNetworkInterface().getAvatar(id);
        avatarbytes.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                Log.e("App", call.request().toString());
                if (r != null && r.body() != null) {
                    //запишим аву
                    try {
                        ByteArrayInputStream bis = new ByteArrayInputStream(r.body().bytes());
                        File af = new File(dir + "/" + id);
                        if (af.exists()) {
                            af.delete();
                        }
                        FileOutputStream fos = new FileOutputStream(af);
                        byte[] read = new byte[1024];
                        int rd;
                        while ((rd = bis.read(read)) > -1) {
                            fos.write(read, 0, rd);
                        }
                        fos.flush();
                        fos.close();
                        bis.close();
                        if (listener != null) {
                            listener.onLoad(id);
                        }
                    } catch (Exception ee) {
                        Log.e("App", "loadAvatar", ee);
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("App", "err", t);

            }
        });

    }

    public void check() {
        Call<String> call = Network.getChatNetworkInterface().getAvatarsLastUpdate();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.body() != null) {
                    AvatarsLastUpdateResponse resp = AvatarsLastUpdateResponse.decode(null, response.body(), AvatarsLastUpdateResponse.class);
                    if (resp != null) {
                        checkNext(resp.getUpdate());
                    } else {
                        checkNextWithoutClear();
                        Log.e("App", "can`t load avatars list");
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("App", "can`t load avatars list", t);
                checkNextWithoutClear();
            }
        });
    }

    private void checkNextWithoutClear() {

                //обновим все авки
                //вначале почистим
                final File dir = new File(context.getFilesDir() + "/avatars/");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                //теперь грузанем список
                Call<String> call = Network.getChatNetworkInterface().getAvatarsList();
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response != null && response.body() != null) {
                            final AvatarsResponse resp = AvatarsResponse.decode(null, response.body(), AvatarsResponse.class);
                            if (resp != null && resp.getAvatars() != null) {
                                Log.e("Avatars", new Gson().toJson(resp));
                                File[]  avasFiles = dir.listFiles();
                                for(int n =0;n<avasFiles.length;n++){
                                    boolean delete = true;
                                    for(AvatarResponse ar: resp.getAvatars()){
                                        if(ar.getId().equals(avasFiles[n].getName())){
                                            delete = false;
                                            break;
                                        }
                                    }
                                    if(delete){
                                        avasFiles[n].delete();
                                    }

                                }
                                for (final AvatarResponse aresp : resp.getAvatars()) {
                                    File af = new File(dir + "/" + aresp.getId());
                                    if (!af.exists()) {
                                        try {
                                            //загрузим аву
                                            Call<ResponseBody> avatarbytes = Network.getChatNetworkInterface().getAvatar(aresp.getId());
                                            avatarbytes.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                                                    if (r != null && r.body() != null) {
                                                        //запишим аву
                                                        try {
                                                            ByteArrayInputStream bis = new ByteArrayInputStream(r.body().bytes());
                                                            File af = new File(dir + "/" + aresp.getId());

                                                            FileOutputStream fos = new FileOutputStream(af);
                                                            byte[] read = new byte[1024];
                                                            int rd;
                                                            while ((rd = bis.read(read)) > -1) {
                                                                fos.write(read, 0, rd);
                                                            }
                                                            fos.flush();
                                                            fos.close();
                                                            bis.close();
                                                        } catch (Exception ee) {
                                                            Log.e("App", "can`t load avatar", ee);

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                                }
                                            });

                                        } catch (Exception e) {
                                            Log.e("App", "can`t load avatars", e);
                                        }
                                    }
                                }
                            } else {
                                Log.e("App", "can`t load avatars");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("App", "can`t load avatars", t);
                    }
                });
    }

    private void checkNext(final Date d) {
        long lastUpdate = new SettingsHelper(context).getAvatarsUpdate();
        if (d != null) {
            if (d.getTime() > lastUpdate) {
                //обновим все авки
                //вначале почистим
                final File dir = new File(context.getFilesDir() + "/avatars/");
                if (dir.exists()) {
                    File[] avs = dir.listFiles();
                    for (File a : avs) {
                        try {
                            a.delete();
                        } catch (Exception e) {
                            Log.e("App", "can`t delete avatar", e);
                        }
                    }
                } else {
                    dir.mkdir();
                }
                //теперь грузанем список
                Call<String> call = Network.getChatNetworkInterface().getAvatarsList();
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response != null && response.body() != null) {
                            final AvatarsResponse resp = AvatarsResponse.decode(null, response.body(), AvatarsResponse.class);
                            if (resp != null && resp.getAvatars() != null) {
                                Log.e("Avatars", new Gson().toJson(resp));
                                boolean error = false;
                                for (final AvatarResponse aresp : resp.getAvatars()) {
                                    try {
                                        //загрузим аву
                                        Call<ResponseBody> avatarbytes = Network.getChatNetworkInterface().getAvatar(aresp.getId());
                                        avatarbytes.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                                                if (r != null && r.body() != null) {
                                                    //запишим аву
                                                    try {
                                                        ByteArrayInputStream bis = new ByteArrayInputStream(r.body().bytes());
                                                        File af = new File(dir + "/" + aresp.getId());

                                                        FileOutputStream fos = new FileOutputStream(af);
                                                        byte[] read = new byte[1024];
                                                        int rd;
                                                        while ((rd = bis.read(read)) > -1) {
                                                            fos.write(read, 0, rd);
                                                        }
                                                        fos.flush();
                                                        fos.close();
                                                        bis.close();
                                                    } catch (Exception ee) {
                                                        Log.e("App", "can`t load avatar", ee);

                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            }
                                        });
                                    } catch (Exception e) {
                                        Log.e("App", "can`t load avatars", e);
                                        error = true;
                                    }
                                    if (!error) {
                                        new SettingsHelper(context).setAvatarsUpdate(d.getTime());
                                    }
                                }
                            } else {
                                Log.e("App", "can`t load avatars");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("App", "can`t load avatars", t);
                    }
                });
            }else{
                checkNextWithoutClear();
            }
        }else{
            checkNextWithoutClear();
        }

    }

    public interface OnAvatarLoadListener {
        void onLoad(String id);
    }
}
